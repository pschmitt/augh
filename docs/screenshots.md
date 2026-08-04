# Screenshot automation (POC)

Captures Play Store listing screenshots with [fastlane screengrab][screengrab], driven by the
`ScreenshotTest` instrumented test (`app/src/androidTest/kotlin/dev/pschmitt/augh/ScreenshotTest.kt`).
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

## Phone screenshots: the wired Zenfone 10

`just screenshots` targets the Zenfone 10 (`R6AIB700W850L7G`) explicitly via
`SCREENGRAB_SPECIFIC_DEVICE`. Setting `ANDROID_SERIAL` alone does **not** reliably steer
screengrab: it has its own device-selection logic that silently ignores `ANDROID_SERIAL` and
just picks whichever device it finds first via its own `adb devices` call. This bit the tablet
capture below (it kept picking the phone) before switching to `SCREENGRAB_SPECIFIC_DEVICE`.

## Tablet screenshots: a local emulator, not a physical device

`just screenshots-tablet` captures the 10-inch bucket (`tenInchScreenshots/`) on a local Pixel
Tablet emulator (API 34 google_apis x86_64), **not** a physical tablet. A real tablet over
network adb (e.g. the Mi Pad 4) is brittle - reconnects, port changes, needing the device
physically present and powered on - exactly the kind of flakiness this automation exists to
avoid. The emulator is scripted and disposable instead: same AVD every time, KVM-accelerated on
this machine (boots in well under a minute), no dependency on real hardware state.

```console
just screenshots-tablet
```

This creates the `augh-tablet` AVD once (`just screenshots-tablet-avd-create`), boots it if
nothing is already listed under `emulator-*` in `adb devices` (`just screenshots-tablet-emulator-start`),
then runs the same `ScreenshotTest` journey with `SCREENGRAB_DEVICE_TYPE=tenInch`. Stop it when
done with `just screenshots-tablet-emulator-stop`.

`avdmanager` (XDG-aware) and `emulator` (not) disagree on the default AVD home when unset - one
lands in `~/.config/.android`, the other looks in `~/.android`. The `screenshots` dev shell pins
`ANDROID_AVD_HOME` to the XDG location explicitly so both tools agree.

## Fixed: Editor was portrait-locked even on tablets

Setting up the tablet emulator surfaced a real app bug, not just infra: `MainActivity.kt`
unconditionally forced `SCREEN_ORIENTATION_PORTRAIT` whenever the app wasn't in Present mode,
including in Editor mode. On a phone that's invisible; on a wide tablet screen it produced a
portrait-shaped window letterboxed with black bars either side (the
`android.window.PROPERTY_COMPAT_ALLOW_RESTRICTED_RESIZABILITY` manifest property wasn't the
culprit - it correctly makes the system honor Present mode's deliberate landscape request instead
of Android 16 silently overriding it). Fixed by only forcing portrait in Editor mode when
`resources.configuration.smallestScreenWidthDp < 600`; large screens now get
`SCREEN_ORIENTATION_UNSPECIFIED` in Editor mode and fill the screen properly. Present mode's
landscape lock is unchanged on all screen sizes.

## Uploading to Play Console

Screenshots and the app icon are uploaded with `gpc` (playconsole-cli), not a fastlane lane:

```console
gpc images delete-all --package dev.pschmitt.augh --locale en-US --type phoneScreenshots --confirm
gpc images upload --package dev.pschmitt.augh --locale en-US --type phoneScreenshots --file <path>
gpc images upload --package dev.pschmitt.augh --locale en-US --type tenInchScreenshots --file <path>
gpc images upload --package dev.pschmitt.augh --locale en-US --type icon --file <path>
```

`gpc doctor` reports a false "credentials not found" - that check has its own bug (confirmed by
reproducing the identical false negative against a scratch config written by `gpc auth login`
itself). Real commands (`images list`, `images upload`, `listings list`, ...) authenticate and
work fine regardless; don't trust `doctor` here.

The app icon needs a 512×512 RGBA PNG; the repo's launcher source
(`app/src/main/res/mipmap/ic_launcher.png`) is 1254×1254 and needs resizing first, e.g.:

```console
magick app/src/main/res/mipmap/ic_launcher.png -resize 512x512 -alpha set icon-512.png
```

## Verified runs, 2026-08-04

- Phone (`phoneScreenshots/`): wired Zenfone 10, `just screenshots-build` then
  `SCREENGRAB_SPECIFIC_DEVICE=R6AIB700W850L7G nix develop .#screenshots --command fastlane
  screenshots`. Both screenshots (`01_editor`, `02_present`) captured and uploaded to the live
  Play Console listing, replacing the previous two images.
- Tablet (`tenInchScreenshots/`): local Pixel Tablet emulator via `just screenshots-tablet`.
  Both screenshots captured filling the full 2560x1600 screen (no letterboxing, after the Editor
  orientation fix above) and uploaded to Play Console (that bucket was previously empty).
- App icon: resized from the 1254×1254 launcher source to 512×512 and uploaded (Play Console had
  no icon set yet).

## Extending beyond the POC

- More screens: add further `Screengrab.screenshot("...")` calls to `ScreenshotTest`.
- More locales: add entries to `locales(...)` in `fastlane/Screengrabfile` — screengrab switches
  the device locale for each one via `LocaleTestRule`, which is already wired into the test.
- 7-inch tablet bucket: the live listing already has manually-captured `sevenInchScreenshots`
  (Mi Pad 4) - could add a smaller emulator profile (e.g. `Nexus 7`) to automate replacing those
  too, following the same pattern as `screenshots-tablet`.
- Uploading via fastlane instead of `gpc`: a further `lane` could call `upload_to_play_store`
  with the captured `fastlane/metadata/android` directory, reusing the same service-account JSON
  documented in the main README's Google Play publishing section - `gpc` was used here instead
  since it was already set up and working.
