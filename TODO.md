# TODO

Completed backlog for aughhhh. Every user-visible request gets a stable `AUG-N` identifier and
an explicit state; IDs are never reused.

## AUG-1: Initial project scaffold + sign-maker MVP

- [x] Compose app module using the sibling apps' current Android stack
- [x] Edit / Present modes, local persistence, fonts, auto-fit typography, colors, and motion
- [x] Accessibility pass with descriptive controls, comfortable targets, and contrast warnings
- [x] Device smoke journey covering edit → present → back to edit

State: **done**, 2026-08-03. Remote lint/check passed; the UiAutomator smoke journey passed on
the Mi Pad 4.

## AUG-2: Funny launcher icon and visual identity

- [x] Add colorful generated meme-inspired launcher artwork
- [x] Wire adaptive foreground/background resources and launcher masking
- [x] Confirm the artwork in the installed app header and launcher resource set

State: **done**, 2026-08-03. The generated artwork is installed, visible in the editor header,
and used by the adaptive launcher resource.

## AUG-3: Remote builds and attached-device deployment

- [x] Nix dev shell with JDK 21, Android SDK 36, platform tools, just, and ktfmt
- [x] CI build, lint, and rolling `latest` release workflows
- [x] Remote `just build` / `just check` recipes targeting rofl-13 / rofl-14
- [x] `just deploy-all` installs the universal debug APK on all attached ADB devices

State: **done**, 2026-08-03. Builds/checks ran on rofl-13 and the debug APK was installed on
R6AIB700W850L7G, mi-pad-4.lan:44972, and px5.lan:39073. The wired Zenfone's secondary-profile
runner does not emit an owner-profile instrumentation result, but installation succeeds.

## AUG-4: Presentation polish

- [x] Named sign presets and recent-sign history
- [x] Text alignment and vertical-position controls
- [x] Keep-screen-awake behavior while presenting
- [x] Orientation-aware typography and editor layout

State: **done**, 2026-08-03. Verified in the Mi Pad editor/presentation session.

## AUG-5: Multi-page signs with swipe navigation

- [x] Persist ordered page messages and the selected edit page
- [x] Add, select, reorder, and delete pages while keeping one page minimum
- [x] Swipe between pages in Present mode with a position indicator
- [x] Persist and apply page transitions

State: **done**, 2026-08-03. Implemented in the editor and Present screen; remote compile/lint
passed.

## AUG-6: Optional cringe page transitions

- [x] Static, Fade, Wipe, Blinds, Checkerboard, and Spin choices
- [x] Animate both swipe directions with reduced-motion fallback
- [x] Persist the selected transition and keep None as the calm default

State: **done**, 2026-08-03. Transition rendering is state-driven and included in the remote
validated APK.

## AUG-7: README polish and Obtainium install path

- [x] Obtainium badge and preconfigured redirect for `dev.pschmitt.aughhhh`
- [x] Document rolling `latest` and debug-install paths
- [x] Add editor and Present screenshots/gallery
- [x] Link GPL-3.0, AGENTS.md, TODO.md, and remote build instructions

State: **done**, 2026-08-03. README and screenshots are present in the public repository.

## AUG-8: Optional tap actions

- [x] Off, Invert, Flash, Sound, and Next page picker
- [x] Implement color inversion and a non-blocking visual flash
- [x] Play a short ToneGenerator beep while respecting silent mode
- [x] Keep the action hint unobtrusive in Present mode

State: **done**, 2026-08-03. Tap actions are persisted and implemented in Present mode.

## AUG-9: Screen rotation and orientation support

- [x] Remove the manifest's fixed orientation restriction
- [x] Preserve mode/page and sign state across recreation
- [x] Recompute editor preview typography for portrait and landscape constraints
- [x] Restore immersive/system-bar policy when leaving Present mode

State: **done**, 2026-08-03. The app requests landscape for Present and normal orientation for
Edit. The Mi Pad image is configured with Android's `ignoreOrientationRequest` policy, so its
hardware display remains portrait during direct-device verification.

## AUG-10: Make blinking more aggressive

- [x] Increase blink contrast and shorten the pulse cycle
- [x] Add blink-intensity control beside speed
- [x] Respect reduced-motion preferences with a static fallback

State: **done**, 2026-08-03. Pulse timing is now substantially faster at 100% and remains
bounded in preview/Present mode.

## AUG-12: Richer default color palette

- [x] Add red and saturated accent swatches
- [x] Use a high-contrast cream-on-red default treatment
- [x] Warn when any selected foreground/background pair is low contrast

State: **done**, 2026-08-03. Palette and contrast checks are live in Looks.

## AUG-13: Animated editor preview

- [x] Preview static, scroll, blink, background blink, invert, and strobe choices
- [x] Keep preview motion bounded and reduced-motion aware
- [x] Show selected animation and transition settings in the editor

State: **done**, 2026-08-03. The live preview uses the same state-driven animation model as
Present mode.

## AUG-14: Background blinking

- [x] Add a background-blink choice
- [x] Interpolate the background while retaining readable text color
- [x] Respect reduced-motion preferences

State: **done**, 2026-08-03.

## AUG-15: Strobe effect

- [x] Add explicitly labeled Strobe mode and an accessibility warning
- [x] Limit the frequency and keep Static immediately available
- [x] Disable strobe when reduced motion is enabled

State: **done**, 2026-08-03.

## AUG-16: Modern default font

- [x] Use Modern sans-serif as the default and migration fallback
- [x] Leave existing saved font selections untouched

State: **done**, 2026-08-03.

## AUG-17: Landscape presentation mode

- [x] Request landscape on Present entry
- [x] Restore the normal orientation policy on exit
- [x] Keep Edit responsive outside Present mode

State: **done**, 2026-08-03. Explicit landscape is requested from the activity; a connected
tablet policy may still ignore orientation requests.

## AUG-18: Use the real logo in the editor header

- [x] Replace the placeholder monogram with the generated aughhhh logo
- [x] Keep the logo readable and accessible at compact header size

State: **done**, 2026-08-03. Confirmed in the Mi Pad editor screenshot.

## AUG-19: Minimal presentation exit control

- [x] Remove the unreadable floating Present footer
- [x] Replace the labeled exit button with a subtle large X
- [x] Keep the close action discoverable to automation/accessibility services

State: **done**, 2026-08-03. The X exposes the `Exit present` content description and passed the
Mi Pad smoke journey.

## AUG-20: Always maximize the sign type

- [x] Remove the font-size selector and auto-fit toggle
- [x] Always recompute the largest fitting size up to the renderer ceiling
- [x] Keep behavior predictable across fonts, pages, orientations, and animation choices

State: **done**, 2026-08-03.

## AUG-21: Streamline the editor chrome

- [x] Remove the Edit/Present tab bar
- [x] Add a presentation icon and shorten the CTA label to `Present`
- [x] Remove the extra background behind the home-page logo

State: **done**, 2026-08-03.

## AUG-22: Put recent messages by the Message field

- [x] Display recent messages immediately above the Message field
- [x] Keep selecting a recent message focused on the current page

State: **done**, 2026-08-03.

## AUG-23: Add visual anchors to editor sections

- [x] Add icons to Pages and the setting-card headings
- [x] Keep icons decorative and preserve text-based section names

State: **done**, 2026-08-03.

## AUG-24: Animated invert mode

- [x] Add Invert to the motion choices
- [x] Animate both text and background colors through the inversion cycle
- [x] Respect reduced-motion preferences

State: **done**, 2026-08-03.

## AUG-25: Iconic option chips

- [x] Give motion, transition, and tap-action values leading icons
- [x] Give font, alignment, vertical-position, and preset values leading icons
- [x] Keep icons decorative and preserve accessible text labels

State: **done**, 2026-08-03.

## AUG-26: Carded editor header and sticky live preview

- [x] Make the app header the first card in the editor
- [x] Remove the empty-looking header/scroll gap
- [x] Keep the live preview sticky at the top after it scrolls into place

State: **done**, 2026-08-03. Confirmed visually on the Mi Pad while scrolling into Looks and
Motion.

## AUG-27: Make motion speed unapologetically fast

- [x] Shorten the pulse cycle at the top of the speed range
- [x] Keep preview and presentation timing consistent and bounded
- [x] Preserve the existing reduced-motion fallback

State: **done**, 2026-08-03. Remote lint/check passed after the final timing change.

## AUG-30: Swipe the live preview between pages

Make the animated preview a direct page-navigation surface so a left/right swipe changes the
selected edit page just like the Present deck gesture.

- [x] Detect horizontal swipes on the preview
- [x] Select the previous/next page with bounds clamping
- [x] Keep the Pages strip and preview selection synchronized

State: **done**, 2026-08-03. Preview swipes share the selected-page state with the Pages strip.

## AUG-31: Remove redundant editor top spacing

Tighten the lazy editor layout so the first header card starts directly below the system inset
instead of leaving a blank area where a conventional top bar would have been.

- [x] Remove redundant list-level top padding
- [x] Preserve status-bar clearance and the first-card layout

State: **done**, 2026-08-03. The editor list now keeps status-bar clearance without redundant top padding.

## AUG-28: Keep preview text faithful to Present

Prevent automatic line wrapping in the live preview when the same sign is shown as a single line
in full-screen Present mode.

- [x] Disable soft wrapping for fitted sign text
- [x] Include width overflow in every fit calculation
- [x] Preserve deliberate newline breaks and scrolling behavior

State: **done**, 2026-08-03. Preview and Present use the same no-soft-wrap fit behavior.

## AUG-29: Start every presentation at page one

Reset the transient presentation cursor whenever Present mode is entered so a new presentation
always begins with the first page.

- [x] Reset the presentation page to index zero on Present entry
- [x] Keep edit-mode page selection independent from presentation start

State: **done**, 2026-08-03. Present entry now always sets its transient page cursor to zero.

## AUG-32: Center selected color swatches

Center color-picker checkmarks inside their circular swatches so the selection state is easy to scan.

- [x] Center the checkmark vertically and horizontally
- [x] Preserve the existing 48dp touch target and color contrast

State: **done**, 2026-08-03. Swatches now use centered Compose text content for their selection mark.

## AUG-33: Trigger tap actions from the live preview

Make the editor preview behave like a small interactive rehearsal surface for the configured tap action.

- [x] Trigger invert, flash, sound, and next-page actions from preview taps
- [x] Keep preview swipes working for page navigation

State: **done**, 2026-08-03. Preview taps now run the configured action while horizontal swipes retain page navigation.
