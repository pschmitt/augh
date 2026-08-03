# aughhhh

<p align="center">
  <img src="app/src/main/res/drawable-nodpi/aughhhh_icon.png" alt="aughhhh icon" width="160">
</p>

<p align="center"><strong>Make a sign. Make it loud. Make it extremely unnecessary.</strong></p>

aughhhh is a colorful Android sign maker for custom full-screen text. Build a deck of pages,
choose an absurd presentation style, and swipe through it when the moment arrives.

## Features

- Edit and Present modes with immersive landscape presentation.
- Multiple pages with add, delete, select, and reorder controls.
- Auto-fit typography that recalculates for the available portrait or landscape canvas.
- Modern, display, editorial, and mono font choices with vertical-position controls.
- Rich text/background palettes, including red, presets, and recent messages.
- Still, scrolling, blinking, background-blinking, invert, and opt-in strobe motion.
- Optional PowerPoint-era page transitions: fade, wipe, blinds, checkerboard, and spin.
- Optional tap actions: invert, flash, beep, or advance to the next page.
- Reduced-motion handling, keep-screen-awake presentation, and local persistence.

## Automation intents

Other Android apps can launch or control aughhhh through the exported `MainActivity`. The action
names are also available as `AughhhhIntents` when depending on the app's source/API constants.

- `dev.pschmitt.aughhhh.action.PRESENT` starts full-screen Present mode. Pass `EXTRA_TEXT` or
  `EXTRA_PAGES`, plus optional styling extras such as `EXTRA_FOREGROUND`, `EXTRA_BACKGROUND`,
  `EXTRA_FONT`, `EXTRA_ANIMATION`, `EXTRA_SPEED`, `EXTRA_BLINK_INTENSITY`, `EXTRA_TRANSITION`,
  `EXTRA_TAP_ACTION`, `EXTRA_VERTICAL_POSITION`, and
  `EXTRA_KEEP_SCREEN_AWAKE`.
- `dev.pschmitt.aughhhh.action.NEXT_PAGE` and `PREVIOUS_PAGE` switch presentation pages.
- `dev.pschmitt.aughhhh.action.TRIGGER_ACTION` performs the configured tap action as if the
  presentation had been tapped.

`EXTRA_PAGES` accepts either a `StringArrayList` or a `String[]`; `EXTRA_TEXT` also accepts the
standard `android.intent.extra.TEXT`. Enum styling values use their names, for example `RED`,
`CREAM`, `SANS`, `BLINK`, `FADE`, or `NEXT_PAGE`. Speed and blink intensity are floats from
`0.15` to `2.0`, and keep-screen-awake is a boolean. A simple shell example:

```sh
adb shell am start -n dev.pschmitt.aughhhh/.MainActivity \
  -a dev.pschmitt.aughhhh.action.PRESENT \
  --es dev.pschmitt.aughhhh.extra.TEXT "PLEASE WAIT" \
  --es dev.pschmitt.aughhhh.extra.FOREGROUND RED
adb shell am start -n dev.pschmitt.aughhhh/.MainActivity \
  -a dev.pschmitt.aughhhh.action.NEXT_PAGE
```

## Screenshots

<p>
  <img src="docs/images/editor.png" alt="aughhhh editor with logo, presets, preview, and page controls" width="48%">
  <img src="docs/images/present.png" alt="aughhhh full-screen presentation" width="48%">
</p>

## Install

Not published on Google Play or F-Droid. Install and auto-update via Obtainium, or download an
APK directly from the [releases page](https://github.com/pschmitt/aughhhh/releases).

[<img src="https://raw.githubusercontent.com/ImranR98/Obtainium/main/assets/graphics/badge_obtainium.png" alt="Get it on Obtainium" height="60">][obtainium-link]

The badge follows the rolling `latest` prerelease and selects the non-debug release APK.

[obtainium-link]: https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22dev.pschmitt.aughhhh%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2Fpschmitt%2Faughhhh%22%2C%22author%22%3A%22pschmitt%22%2C%22name%22%3A%22aughhhh%22%2C%22preferredApkIndex%22%3A0%2C%22additionalSettings%22%3A%22%7B%5C%22includePrereleases%5C%22%3Atrue%2C%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22apkFilterRegEx%5C%22%3A%5C%22aughhhh-.%2A-release%5C%5C%5C%5C.apk%24%5C%22%2C%5C%22invertAPKFilter%5C%22%3Afalse%2C%5C%22autoApkFilterByArch%5C%22%3Atrue%2C%5C%22trackOnly%5C%22%3Afalse%7D%22%7D

## Development

Gradle builds intentionally run on `rofl-13` or `rofl-14`, not on the local workstation:

```sh
just check
just build
just deploy-all
```

`just deploy-all` fetches the remote debug APK and installs it on every attached ADB device.
The debug application id is `dev.pschmitt.aughhhh.debug`.

See [AGENTS.md](AGENTS.md) for repository conventions and [TODO.md](TODO.md) for the running
backlog. This project is licensed under [GPL-3.0](LICENSE).
