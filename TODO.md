# TODO

Running backlog/changelog for aughhhh. Each feature or fix gets one sequential `## AUG-N:`
entry. IDs are never reused or renumbered. Every entry keeps an explicit state so the next
session can pick up without guessing what is actually finished.

## AUG-1: Initial project scaffold + sign-maker MVP

Build the first usable app: a Material 3 editor for custom text, a full-screen presentation mode,
local persistence, configurable type, colors, and optional motion.

- [x] Single `:app` Compose module using the sibling apps' current AGP/Kotlin/Material 3 stack
- [x] Edit / Present mode switch with immersive full-screen presentation
- [x] Persist message and presentation settings across restarts
- [x] Configurable font family, font size, text color, and background color
- [x] Still, scrolling, and blinking animation options with speed control
- [ ] Accessibility pass: content descriptions, larger touch targets, and contrast checks
- [ ] Instrumented smoke test covering edit → present → back to edit

State: **in_progress**, 2026-08-03. Core UI is implemented; accessibility and device smoke
coverage remain before calling the MVP done.

## AUG-2: Funny launcher icon and visual identity

Replace the temporary text mark with a colorful, meme-inspired launcher icon that remains legible
at small sizes and works with Android launcher masking.

- [x] Add generated icon asset and launcher resource wiring
- [ ] Add adaptive icon foreground/background resources
- [ ] Confirm icon and splash rendering on attached devices

State: **in_progress**, 2026-08-03. The generated launcher artwork is integrated; adaptive-mask
resources and device confirmation remain.

## AUG-3: Remote builds and attached-device deployment

Keep Gradle off the local workstation. Build and validate on `rofl-13` or `rofl-14`, fetch the APK,
then install the result on every ADB device currently attached to this workstation.

- [x] Nix dev shell with JDK 21, Android SDK 36, platform tools, just, and ktfmt
- [x] CI build, lint, and rolling `latest` release workflows using JDK 21 and Gradle caching
- [x] Remote `just build` / `just check` recipes targeting `rofl-13` / `rofl-14`
- [x] `just deploy-all` installs the universal debug APK on all connected ADB devices

State: **in_progress**, 2026-08-03. Remote build/check and installation are working; launch
verification is green on the Mi Pad 4 and px5, while the Zenfone's secondary Android profile
blocks shell activity resolution and needs a manual owner-profile check.

## AUG-4: Presentation polish

Make the app feel delightful in real use: quick presets, robust typography at extreme sizes,
orientation-aware layout, and a clean handoff into presentation mode.

- [ ] Add named sign presets and a recent-sign history
- [ ] Add text alignment and vertical-position controls
- [ ] Add keep-screen-awake behavior while presenting
- [ ] Test landscape/tablet layouts and rotation state restoration

State: **planned**.

## AUG-17: Landscape presentation mode

Switch to sensor-landscape automatically when entering Present mode, giving signs the widest
canvas by default while still allowing the device to rotate between landscape orientations.

- [ ] Request sensor-landscape on Present entry
- [ ] Restore the user's normal orientation policy on exit
- [ ] Keep Edit mode responsive in portrait and landscape

State: **planned**.

## AUG-16: Modern default font

Use the Modern sans-serif face as the first-run default while keeping the other font choices
available for more theatrical signs.

- [ ] Change the default and migration fallback to Modern
- [ ] Keep existing user-selected fonts untouched

State: **planned**.

## AUG-14: Background blinking

Add an optional animation that blinks the presentation background while leaving text legible.

- [ ] Add a background-blink animation choice
- [ ] Keep text/background contrast readable through the cycle
- [ ] Respect reduced-motion preferences

State: **planned**.

## AUG-15: Strobe effect

Add an explicitly labeled, opt-in strobe mode for maximum ridiculousness, with warnings and an
automatic reduced-motion fallback.

- [ ] Add a strobe animation choice with an accessibility warning
- [ ] Limit the strobe frequency and provide an immediate off path
- [ ] Disable strobe when reduced-motion is enabled

State: **planned**.

## AUG-12: Richer default color palette

Make the initial visual treatment feel more saturated and expressive, including a proper red
option for urgent or dramatic signs.

- [ ] Add red and richer accent swatches
- [ ] Use a colorful high-contrast default sign treatment
- [ ] Check contrast for every foreground/background combination

State: **planned**.

## AUG-13: Animated editor preview

Make the preview card reflect the selected presentation animation so users can tune motion before
entering full-screen mode.

- [ ] Preview static, scrolling, and blinking text in the editor
- [ ] Keep preview motion bounded and battery-friendly
- [ ] Show the selected animation and transition settings clearly

State: **planned**.

## AUG-10: Make blinking more aggressive

Tune the Blink animation so it feels more intentionally obnoxious and attention-grabbing while
remaining readable and offering a calmer alternative for people who need less visual motion.

- [ ] Increase the blink contrast and shorten the default cycle
- [ ] Add a blink-intensity control alongside animation speed
- [ ] Respect reduced-motion preferences with a gentler fallback

State: **planned**.

## AUG-8: Optional tap actions

Let a tap on the presented sign trigger an optional attention action, configured separately from
page swipes. Candidate actions include inverting foreground/background colors, briefly flashing
the sign, playing a short sound effect, or advancing to the next page.

- [ ] Add a tap-action picker with a clear Off option
- [ ] Implement color inversion and a short visual flash without interrupting page animation
- [ ] Add a small bundled sound effect with a mute/respect-device-silent-mode path
- [ ] Keep tap actions discoverable but unobtrusive in Present mode

State: **planned**.

## AUG-9: Screen rotation and orientation support

Make Edit and Present modes behave correctly across portrait/landscape rotation and different
screen sizes, retaining the current page, text, styling, and animation configuration.

- [ ] Remove the fixed orientation restriction from the activity
- [ ] Preserve the selected edit/present page and transient presentation state on recreation
- [ ] Tune preview/editor layout for landscape phones and tablets
- [ ] Confirm immersive system-bar handling after rotation on physical devices

State: **planned**.

## AUG-7: README polish and Obtainium install path

Turn the README into a friendly project landing page with screenshots, feature highlights, the
rolling-release install story, and an Obtainium redirect badge linked to this public repository.

- [ ] Add an Obtainium badge and preconfigured redirect for `dev.pschmitt.aughhhh`
- [ ] Document the `latest` release APK and debug-install options
- [ ] Add a small screenshot/gallery section after device verification
- [ ] Link the GPL-3.0 license and contribution/build instructions from the landing page

State: **planned**.

## AUG-5: Multi-page signs with swipe navigation

Allow a sign deck to contain multiple editable pages. Present mode should move between pages with
left/right swipes, while Edit mode makes the selected page obvious and allows pages to be deleted.

- [x] Persist an ordered list of page messages and the selected edit page
- [x] Add, select, and delete pages in Edit mode, keeping at least one page
- [x] Swipe left/right through pages in Present mode with a position indicator
- [ ] Add a page-reorder interaction for longer decks
- [ ] Add a presentation transition between pages

State: **in_progress**, 2026-08-03. Core multi-page editing and swipe navigation are implemented;
reordering and transition polish remain.

## AUG-6: Optional cringe page transitions

Add an opt-in transition setting for page changes in Present mode. The visual language should be
deliberately over-the-top: PowerPoint-era wipes, checkerboards, blinds, spinning text, and other
lovingly embarrassing presentation effects, while keeping a calm no-transition option available.

- [ ] Add a transition picker with Static / Fade / Wipe / Blinds / Checkerboard / Spin options
- [ ] Animate transitions in both swipe directions without dropping frames
- [ ] Persist the selected transition and expose a no-motion-friendly default
- [ ] Add a reduced-motion behavior that disables the deliberately cringe effects

State: **planned**.
