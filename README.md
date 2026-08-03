# aughhhh

<p align="center">
  <img src="app/src/main/res/drawable-nodpi/aughhhh_icon.png" alt="aughhhh icon" width="160">
</p>

<p align="center"><strong>Make a sign. Make it loud. Make it extremely unnecessary.</strong></p>

aughhhh is a colorful Android sign maker for custom full-screen text. Build a deck of pages,
choose an absurd presentation style, and swipe through it when the moment arrives.

## Features

- Edit and Present modes with immersive, sensor-landscape presentation.
- Multiple pages with add, delete, select, and reorder controls.
- Auto-fit typography that recalculates for the available portrait or landscape canvas.
- Modern, display, editorial, and mono font choices with alignment and vertical-position controls.
- Rich text/background palettes, including red, presets, and recent messages.
- Still, scrolling, blinking, background-blinking, and opt-in strobe motion.
- Optional PowerPoint-era page transitions: fade, wipe, blinds, checkerboard, and spin.
- Optional tap actions: invert, flash, beep, or advance to the next page.
- Reduced-motion handling, keep-screen-awake presentation, and local persistence.

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
