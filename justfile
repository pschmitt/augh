set shell := ["bash", "-euo", "pipefail", "-c"]

application_id := "dev.pschmitt.aughhhh.debug"
remote_host := env_var_or_default("AUGHHHH_REMOTE_HOST", "rofl-13.brkn.lol")
remote_path := env_var_or_default("AUGHHHH_REMOTE_PATH", "~/build/aughhhh")
local_dist := env_var_or_default("AUGHHHH_DIST_DIR", "./dist")
default_abi := env_var_or_default("AUGHHHH_ABI", "arm64-v8a")

default:
    @just --list

# Sync only source/configuration to the remote build host; never copy local build output.
sync host=remote_host:
    rsync -az --delete --exclude='.git' --exclude='**/build/' --exclude='.gradle/' --exclude='**/.gradle/' ./ {{host}}:{{remote_path}}/

# All Gradle work happens inside the remote Nix dev shell.
gradle host=remote_host *tasks: (sync host)
    ssh {{host}} 'cd {{remote_path}} && nix develop --command ./gradlew {{tasks}}'

build variant="debug" host=remote_host:
    #!/usr/bin/env bash
    set -euo pipefail
    task=assembleDebug
    if [[ "{{variant}}" == "release" ]]; then
      task=assembleRelease
    fi
    just sync "{{host}}"
    ssh "{{host}}" "cd {{remote_path}} && nix develop --command ./gradlew :app:$task"

fetch variant="debug" host=remote_host abi=default_abi:
    #!/usr/bin/env bash
    set -euo pipefail
    mkdir -p "{{local_dist}}"
    scp "{{host}}:{{remote_path}}/app/build/outputs/apk/{{variant}}/app-{{abi}}-{{variant}}.apk" "{{local_dist}}/"

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
    apk="{{local_dist}}/app-{{default_abi}}-{{variant}}.apk"
    mapfile -t targets < <(adb devices | awk '$2 == "device" { print $1 }')
    if [[ "${#targets[@]}" -eq 0 ]]; then
      printf 'No attached ADB devices are ready for installation\n' >&2
      exit 1
    fi
    for target in "${targets[@]}"; do
      printf 'Installing %s on %s\n' "$apk" "$target"
      adb -s "$target" install -r "$apk"
    done

# vim: set ft=sh et ts=2 sw=2 :
