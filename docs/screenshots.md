# Screenshot automation (POC)

Captures Play Store listing screenshots with [fastlane screengrab][screengrab], driven by the
`ScreenshotTest` instrumented test (`app/src/androidTest/kotlin/dev/pschmitt/aughhhh/ScreenshotTest.kt`).
Scope is intentionally narrow for now: **en-US only**, editor + full-screen present.

Fastlane regenerates `fastlane/README.md` itself on every run, so this doc lives outside
`fastlane/` to avoid being overwritten.

[screengrab]: https://docs.fastlane.tools/actions/screengrab/

## How it fits the existing build split

Per `AGENTS.md`, Gradle/Android SDK work stays on the remote build hosts. `just screenshots`
respects that split:

1. `just screenshots-build` builds `app-debug.apk` and `app-debug-androidTest.apk` remotely (same
   as `just build`) and `scp`s both into `./dist/`.
2. `fastlane screenshots` (via `nix develop .#screenshots`) only installs those prebuilt APKs and
   drives them over `adb` — no local Gradle/SDK evaluation, consistent with `just deploy-all`
   already using local `adb` today.

Run the whole thing with:

```console
just screenshots
```

Output lands in `fastlane/metadata/android/en-US/images/phoneScreenshots/` (screengrab's default
layout; the device bucket subfolder name depends on the target's screen size).

## Emulator vs. real device

`fastlane screengrab` talks to whatever `adb` currently targets:

- **Emulator**: start one first (e.g. `emulator -avd <name>`), then run `just screenshots`.
  Emulators are usually rooted, so you can flip `use_adb_root(true)` in
  `fastlane/Screengrabfile` to unlock screengrab's root-only tricks (status bar clearing, etc.).
- **Real device**: plug in or `adb connect` one of the existing devices (see `just deploy-all`).
  Non-rooted real devices can't do `adb root`, which is why `use_adb_root` stays at its default
  (`false`) here.
- **Multiple targets attached**: set `ANDROID_SERIAL=<serial>` before running `just screenshots`,
  same as targeting a specific device with `adb -s`.

## Verified POC run, 2026-08-04

Ran end to end against the wired Zenfone 10 (`R6AIB700W850L7G`):

```console
just screenshots-build
ANDROID_SERIAL=R6AIB700W850L7G nix develop .#screenshots --command fastlane screenshots
```

Both screenshots (`01_editor`, `02_present`) were captured and pulled into
`fastlane/metadata/android/en-US/images/phoneScreenshots/`.

## Extending beyond the POC

- More screens: add further `Screengrab.screenshot("...")` calls to `ScreenshotTest`.
- More locales: add entries to `locales(...)` in `fastlane/Screengrabfile` — screengrab switches
  the device locale for each one via `LocaleTestRule`, which is already wired into the test.
- More device classes (7"/10" tablet buckets for the Play Store listing): repeat
  `just screenshots` against each physical device or emulator profile; screengrab buckets output
  by the target's screen size automatically.
- Uploading straight to Play Console: a further `lane` could call `upload_to_play_store` with the
  captured `fastlane/metadata/android` directory, reusing the same service-account JSON documented
  in the main README's Google Play publishing section.
