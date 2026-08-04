set shell := ["bash", "-euo", "pipefail", "-c"]

application_id := "dev.pschmitt.augh.debug"
remote_host := env_var_or_default("AUGH_REMOTE_HOST", "rofl-13.brkn.lol")
remote_path := env_var_or_default("AUGH_REMOTE_PATH", "~/build/augh")
local_dist := env_var_or_default("AUGH_DIST_DIR", "./dist")
source_commit := `git rev-parse HEAD`
zenfone_serial := env_var_or_default("ZENFONE_SERIAL", "R6AIB700W850L7G")

default:
    @just --list

# Sync only source/configuration to the remote build host; never copy local build output.
sync host=remote_host:
    rsync -az --delete --exclude='.git' --exclude='**/build/' --exclude='.gradle/' --exclude='**/.gradle/' ./ {{host}}:{{remote_path}}/

# All Gradle work happens inside the remote Nix dev shell.
gradle host=remote_host *tasks: (sync host)
    ssh {{host}} "cd {{remote_path}} && nix develop --command ./gradlew {{tasks}} -PaughCommit={{source_commit}}"

build variant="debug" host=remote_host:
    #!/usr/bin/env bash
    set -euo pipefail
    task=assembleDebug
    if [[ "{{variant}}" == "release" ]]; then
      task=assembleRelease
    fi
    just sync "{{host}}"
    ssh "{{host}}" "cd {{remote_path}} && nix develop --command ./gradlew :app:$task -PaughCommit={{source_commit}}"

fetch variant="debug" host=remote_host:
    #!/usr/bin/env bash
    set -euo pipefail
    mkdir -p "{{local_dist}}"
    scp "{{host}}:{{remote_path}}/app/build/outputs/apk/{{variant}}/app-{{variant}}.apk" "{{local_dist}}/"

build-fetch variant="debug" host=remote_host:
    just build {{variant}} {{host}}
    just fetch {{variant}} {{host}}

check host=remote_host: (gradle host "ktfmtCheck testDebugUnitTest lintDebug")

clean host=remote_host: (gradle host "clean")

# Install a fetched APK on every currently attached ADB device. This is the only local Android
# operation here; the artifact itself was built remotely above.
deploy-all variant="debug" host=remote_host:
    #!/usr/bin/env bash
    set -euo pipefail
    just build-fetch "{{variant}}" "{{host}}"
    apk="{{local_dist}}/app-{{variant}}.apk"
    mapfile -t targets < <(adb devices | awk '$2 == "device" { print $1 }')
    if [[ "${#targets[@]}" -eq 0 ]]; then
      printf 'No attached ADB devices are ready for installation\n' >&2
      exit 1
    fi
    for target in "${targets[@]}"; do
      printf 'Installing %s on %s\n' "$apk" "$target"
      adb -s "$target" install -r "$apk"
    done

# Build the debug app and its instrumentation APK remotely, then fetch both locally for screengrab.
screenshots-build host=remote_host: (gradle host "assembleDebug assembleDebugAndroidTest")
    #!/usr/bin/env bash
    set -euo pipefail
    mkdir -p "{{local_dist}}"
    scp "{{host}}:{{remote_path}}/app/build/outputs/apk/debug/app-debug.apk" "{{local_dist}}/"
    scp "{{host}}:{{remote_path}}/app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk" "{{local_dist}}/"

# Capture Play Store screenshots (en-US) via fastlane screengrab against the Zenfone 10. Setting
# ANDROID_SERIAL alone does NOT reliably steer screengrab when multiple devices/emulators are
# attached - it has its own device-selection logic (SCREENGRAB_SPECIFIC_DEVICE) that silently
# ignores ANDROID_SERIAL and just picks whichever device it finds first, which bit us once the
# tablet emulator could also be attached. See docs/screenshots.md.
screenshots host=remote_host: (screenshots-build host)
    SCREENGRAB_SPECIFIC_DEVICE={{zenfone_serial}} nix develop .#screenshots --command fastlane screenshots

# --- Tablet screenshots (emulator, not a physical device) ------------------
#
# A real tablet over network adb (e.g. the Mi Pad 4) is brittle - reconnects, port changes,
# needing the device physically present and powered on. An emulator is scripted and disposable
# instead: same AVD every time, no dependency on real hardware state.

screenshots_tablet_avd := env_var_or_default("AUGH_TABLET_AVD", "augh-tablet")

# Create the tablet AVD once (Pixel Tablet profile, API 34 google_apis x86_64). Safe to re-run;
# skips if it already exists.
screenshots-tablet-avd-create:
    #!/usr/bin/env bash
    set -euo pipefail
    nix develop .#screenshots --command bash -euo pipefail -c '
      if avdmanager list avd | grep -q "Name: {{screenshots_tablet_avd}}$"; then
        echo "AVD {{screenshots_tablet_avd}} already exists"
        exit 0
      fi
      echo "no" | avdmanager create avd \
        --name {{screenshots_tablet_avd}} \
        --package "system-images;android-34;google_apis;x86_64" \
        --device "pixel_tablet"
    '

# Start the tablet emulator in the background (hardware-accelerated via /dev/kvm) and wait for it
# to finish booting. Prints its adb serial on stdout.
screenshots-tablet-emulator-start:
    #!/usr/bin/env bash
    set -euo pipefail
    serial=$(adb devices | awk '/^emulator-/ { print $1; exit }')
    if [ -n "$serial" ]; then
      echo "$serial"
      exit 0
    fi
    nix develop .#screenshots --command bash -c '
      nohup emulator -avd {{screenshots_tablet_avd}} -no-window -no-snapshot -no-audio -no-boot-anim \
        -gpu swiftshader_indirect >/tmp/augh-tablet-emulator.log 2>&1 &
      disown
    '
    for _ in $(seq 1 60); do
      serial=$(adb devices | awk '/^emulator-/ { print $1; exit }')
      [ -n "$serial" ] && break
      sleep 2
    done
    [ -n "$serial" ] || { echo "emulator did not register with adb" >&2; exit 1; }
    adb -s "$serial" wait-for-device
    until [ "$(adb -s "$serial" shell getprop sys.boot_completed | tr -d '\r')" = "1" ]; do
      sleep 2
    done
    echo "$serial"

# Stop whichever tablet emulator is currently running, if any.
screenshots-tablet-emulator-stop:
    #!/usr/bin/env bash
    set -euo pipefail
    serial=$(adb devices | awk '/^emulator-/ { print $1; exit }')
    [ -n "$serial" ] && adb -s "$serial" emu kill || true

# Capture Play Store tablet screenshots (10-inch bucket) on a local Pixel Tablet emulator, reusing
# the same ScreenshotTest journey as `screenshots`. Output lands in
# fastlane/metadata/android/en-US/images/tenInchScreenshots/, separate from the phone screenshots
# so one capture never overwrites the other.
screenshots-tablet host=remote_host: (screenshots-build host) (screenshots-tablet-avd-create)
    #!/usr/bin/env bash
    set -euo pipefail
    serial=$(just screenshots-tablet-emulator-start)
    SCREENGRAB_SPECIFIC_DEVICE="$serial" SCREENGRAB_DEVICE_TYPE=tenInch \
      nix develop .#screenshots --command fastlane screenshots

# vim: set ft=sh et ts=2 sw=2 :
