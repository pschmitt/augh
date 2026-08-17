# AUGH! repository instructions

See `.just/android-app-ci/AGENTS-shared.md` for the fleet-wide task-tracking convention, dev
environment (`nix develop`/`git-hooks.nix`), CI-is-the-sole-lint-authority rule, and physical test
device docs (this app has all three: Zenfone 10, Mi Pad 4, Pixel 5) - read it alongside this file,
not instead of it.

## Project shape

AUGH! is a small Kotlin/Jetpack Compose Android app for creating and presenting custom text.
The main package is `dev.pschmitt.augh`, the debug application id is
`dev.pschmitt.augh.debug`, and the app is licensed under GPL-3.0.

Keep the app focused: polished Material 3 interaction, fast composition, readable typography,
and a reliable transition between Edit and Present modes matter more than adding a large
framework or network service.

## Build and validation

- This project's `TODO.md` prefix is `AUG-N`. Read `TODO.md` before starting work and add or
  update an entry for user-visible work.
- Do not run Gradle builds, tests, lint, or Android SDK evaluations on this workstation.
- Use `just build`, `just check`, or the narrower remote recipes; they sync the checkout and run
  inside the Nix dev shell on `rofl-13.brkn.lol` by default. `rofl-14.brkn.lol` is the fallback.
- Use `just deploy-all` after a successful remote debug build to install it on all three fleet
  test devices (Zenfone 10, Mi Pad 4, Pixel 5) at once - see the shared doc's "Physical test
  devices" section. Only target a single named device (`deploy-zenfone`/`deploy-mipad`/
  `deploy-px5`) when there's a specific reason to.
- Use `nix develop --command nixfmt flake.nix` for Nix formatting when needed. Keep Kotlin in
  the repository's ktfmt style and use `./gradlew ktfmtCheck` only through the remote recipes -
  see the shared doc for the `ktfmt-diff-patch` retrieval procedure if CI's `Lint` job fails.
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
