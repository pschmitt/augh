set shell := ["bash", "-euo", "pipefail", "-c"]

application_id := "dev.pschmitt.augh.debug"
remote_host := env_var_or_default("AUGH_REMOTE_HOST", "rofl-13.brkn.lol")

# Empty for the main checkout; "-<worktree-dirname>" when run from a linked git worktree (e.g. one
# of Claude's isolated agent worktrees under .claude/worktrees/). Keeps parallel worktree agents
# from clobbering each other's remote sync directory mid-build.
worktree_suffix := `gd=$(git rev-parse --git-dir); gcd=$(git rev-parse --git-common-dir); if [ "$gd" != "$gcd" ]; then basename "$(git rev-parse --show-toplevel)" | sed 's/^/-/'; fi`

remote_path := env_var_or_default("AUGH_REMOTE_PATH", "~/build/augh" + worktree_suffix)
local_dist := env_var_or_default("AUGH_DIST_DIR", "./dist")

# No per-ABI split - a single universal APK (app-debug.apk / app-release.apk, no ABI in the name).
default_abi := ""

source_commit := `git rev-parse HEAD`
gradle_extra_props := "-PaughCommit=" + source_commit

zenfone_serial := env_var_or_default("ZENFONE_SERIAL", "R6AIB700W850L7G")

mipad_host := env_var_or_default("MIPAD_HOST", "mi-pad-4.lan")
mipad_ssh_port := env_var_or_default("MIPAD_SSH_PORT", "8022")
mipad_adb_port := env_var_or_default("MIPAD_ADB_PORT", "5555")

px5_host := env_var_or_default("PX5_HOST", "px5.lan")

# No CI signing keystore configured - `build variant=release` falls back to build.gradle.kts's own
# (debug-key) release signing config. rbw_keystore_entry/keystore_*_attachment/ci_tmp_dir_name are
# unused with this off, but single-module.just's `build` recipe still needs them defined.
enable_release_signing := "false"
rbw_keystore_entry := ""
keystore_jks_attachment := ""
keystore_env_attachment := ""
ci_tmp_dir_name := ".augh-ci-tmp"

# List all available recipes. Must stay the first recipe in this file (not just the first line
# overall) - `just` only considers recipes written directly here, not ones pulled in via the
# import below, when deciding what a bare `just` invocation runs.
default:
    @just --list

# Recipes shared across the app fleet: format/nix-fmt/nix-lint/screenshots-upload (common.just, all
# 4 apps) and the remote sync/build/deploy pipeline - sync/gradle/build/fetch/build-fetch/clean/
# lint/test plus the zenfone-*/mipad-*/px5-*/deploy-all device recipes (single-module.just, the 3
# single-Gradle-module apps). See pschmitt/android-app-ci's just/ for the source of truth.
# Pulled in via a git submodule at .just/android-app-ci (tracking that repo's main branch);
# `just update-common` (defined at the bottom of this file) refreshes it. The devShell's shellHook
# auto-runs `git submodule update --init` on every `nix develop` entry, so a fresh git worktree
# never needs a manual `--init` step. AUGH! didn't have format/nix-fmt/nix-lint,
# zenfone/mipad/px5 device recipes, or the standard `lint`/`test`/`build-fetch` before this - it
# now does, for free. `deploy-all` changes meaning: it used to install on every attached ADB
# device, now it installs on the same 3 named devices (Zenfone 10, Mi Pad 4, Pixel 5) the sibling
# apps target - it already hardcoded the Zenfone for `screenshots` below, so this converges rather
# than invents.
import '.just/android-app-ci/just/common.just'
import '.just/android-app-ci/just/single-module.just'

check host=remote_host: (gradle host "ktfmtCheck testDebugUnitTest lintDebug")

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

# --- Play Console uploads ---------------------------------------------------

play_package := "dev.pschmitt.augh"

# Flatten and upload the app icon used by the launcher and README (not locale-scoped, so kept
# separate from the screenshot upload above).
play-icon-upload:
    #!/usr/bin/env bash
    set -euo pipefail
    source_icon="app/src/main/res/mipmap/ic_launcher.png"
    if [[ ! -f "$source_icon" ]]
    then
      printf 'Icon source not found: %s\n' "$source_icon" >&2
      exit 1
    fi
    if ! command -v magick >/dev/null
    then
      printf 'ImageMagick `magick` is required to flatten the icon\n' >&2
      exit 1
    fi
    if ! command -v gpc >/dev/null
    then
      printf 'gpc (playconsole-cli) is required for Play Console uploads\n' >&2
      exit 1
    fi
    if ! gpc apps list --output json | rg -q '"package_name":"{{play_package}}"'
    then
      printf 'Play Console package %s was not found via `gpc apps list`\n' "{{play_package}}" >&2
      exit 1
    fi
    temp_dir=$(mktemp -d)
    trap 'rm -rf "$temp_dir"' EXIT
    magick "$source_icon" -resize 512x512 -alpha set "$temp_dir/augh-icon.png"
    gpc --package {{play_package}} images upload \
      --locale en-US \
      --type icon \
      --file "$temp_dir/augh-icon.png"

# Upload the already-committed feature graphic
# (fastlane/metadata/android/en-US/images/featureGraphic.png, 1024x500) to the Play Console
# listing. Not locale-scoped, so kept separate from the screenshot upload above.
play-feature-graphic-upload:
    #!/usr/bin/env bash
    set -euo pipefail
    graphic="fastlane/metadata/android/en-US/images/featureGraphic.png"
    if [[ ! -f "$graphic" ]]
    then
      printf 'Feature graphic not found: %s\n' "$graphic" >&2
      exit 1
    fi
    if ! command -v gpc >/dev/null
    then
      printf 'gpc (playconsole-cli) is required for Play Console uploads\n' >&2
      exit 1
    fi
    if ! gpc apps list --output json | rg -q '"package_name":"{{play_package}}"'
    then
      printf 'Play Console package %s was not found via `gpc apps list`\n' "{{play_package}}" >&2
      exit 1
    fi
    gpc --package {{play_package}} images upload \
      --locale en-US \
      --type featureGraphic \
      --file "$graphic"

# --- Shared recipes (pschmitt/android-app-ci) -------------------------------

# Advance the .just/android-app-ci submodule to the tip of its tracked branch (main) and stage the
# result - review the diff like any other dependency bump before committing it.
update-common:
    git submodule update --remote .just/android-app-ci
    git add .just/android-app-ci

# vim: set ft=sh et ts=2 sw=2 :
