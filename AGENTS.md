# aughhhh repository instructions

## Project shape

aughhhh is a small Kotlin/Jetpack Compose Android app for creating and presenting custom text.
The main package is `dev.pschmitt.aughhhh`, the debug application id is
`dev.pschmitt.aughhhh.debug`, and the app is licensed under GPL-3.0.

Keep the app focused: polished Material 3 interaction, fast composition, readable typography,
and a reliable transition between Edit and Present modes matter more than adding a large
framework or network service.

## Build and validation

- Read `TODO.md` before starting work and add or update an `AUG-N` entry for user-visible work.
- Do not run Gradle builds, tests, lint, or Android SDK evaluations on this workstation.
- Use `just build`, `just check`, or the narrower remote recipes; they sync the checkout and run
  inside the Nix dev shell on `rofl-13.brkn.lol` by default. `rofl-14.brkn.lol` is the fallback.
- Use `just deploy-all` after a successful remote debug build to fetch the APK and install it on
  every ADB target currently reporting `device`.
- Use `nix develop --command nixfmt flake.nix` for Nix formatting when needed. Keep Kotlin in
  the repository's ktfmt style and use `./gradlew ktfmtCheck` only through the remote recipes.
- Prefer focused checks before a full validation pass. Record device/build evidence in `TODO.md`.

## Editing and design

- Keep changes minimal and preserve unrelated user work.
- Use `apply_patch` for text-file edits and never introduce trailing whitespace.
- Prefer Compose Material 3 components and state-driven UI. Persist user-facing sign settings.
- Presentation mode must be immersive, legible, and reversible with the system back action.
- Keep touch targets comfortable and verify foreground/background contrast for each palette pair.
- Use generated raster art only for the launcher artwork; keep UI icons and simple shapes native to
  Compose or Android resources.

## Git and GitHub

- This repository is public and uses GPL-3.0.
- Do not commit secrets, signing keys, generated build output, or local SDK configuration.
- Keep commits scoped and describe behavior changes clearly.

## Shell recipes

Shell recipes use bash with strict error handling, two-space indentation, and clear error output.
Use `[[ ... ]]` for tests, quote paths, and avoid broad or destructive filesystem operations.

# vim: set ft=markdown et ts=2 sw=2 :
