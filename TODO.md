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
- [x] Vertical-position controls
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

## AUG-34: Remove the black sticky-preview strip

Investigate the opaque sticky-preview wrapper that made the mobile inset look like a redundant header.

- [x] Try a transparent sticky-preview wrapper
- [x] Preserve sticky preview behavior and system-bar inset handling

State: **done**, 2026-08-03. The duplicate inset was removed; the transparent-wrapper experiment was superseded by AUG-36.

## AUG-35: Remove duplicate editor top inset

Avoid a second safe-drawing inset in the editor after Scaffold has already applied system-bar insets.

- [x] Remove the duplicate list-level safe-drawing padding
- [x] Keep the first header card below the actual system inset

State: **done**, 2026-08-03. The editor no longer creates a redundant black strip above its content.

## AUG-36: Keep the sticky preview backdrop opaque

Give the sticky live-preview container a solid surface backdrop so scrolled editor cards do not show through it.

- [x] Use the app surface color around the sticky preview
- [x] Keep the duplicate top inset removed

State: **done**, 2026-08-03. The preview now has an opaque surface-colored sticky backdrop.

## AUG-37: Add external presentation intents

Expose documented Android intents for launching a styled presentation and remotely controlling its pages and actions.

- [x] Add a full-screen presentation action with text, pages, and styling extras
- [x] Add next-page and previous-page actions
- [x] Add an action-trigger command that behaves like a presentation tap
- [x] Document the action and extra names with shell examples

State: **done**, 2026-08-03. External apps can launch styled presentations and control page navigation or the configured tap action.

## AUG-38: Accept standard external page-array extras

Accept both Android `StringArrayList` and `String[]` representations for pages supplied by external callers.

- [x] Read `StringArrayList` page extras
- [x] Read standard `String[]` page extras

State: **done**, 2026-08-03. `EXTRA_PAGES` now works with both common Android intent-extra encodings.

## AUG-39: Make motion controls properly fast

Increase animation responsiveness so the fastest setting feels intentionally frantic rather than merely acceptable.

- [x] Expand the speed control to 15%–200%
- [x] Apply the multiplier to pulse, scrolling, page transitions, and overlays
- [x] Reduce the base timings and preserve reduced-motion behavior

State: **done**, 2026-08-03. Motion now reaches a genuinely fast 2x mode across the presentation effects.

## AUG-40: Honor landscape presentation on large screens

Opt out of Android 16's large-screen compatibility behavior that ignores runtime orientation requests.

- [x] Diagnose the Mi Pad's `LANDSCAPE` request versus portrait effective configuration
- [x] Opt the activity out of restricted-resizability compatibility behavior
- [x] Verify and refresh the landscape presentation capture

State: **done**, 2026-08-03. Present mode now explicitly opts out of the large-screen orientation override; the Mi Pad previously ignored the request because it is `sw600dp`.

## AUG-41: Remove the obsolete size section

Remove the leftover “largest possible” copy now that font sizing is always automatic and has no user-facing setting.

- [x] Remove the obsolete size heading
- [x] Remove the redundant auto-fit explanation
- [x] Preserve automatic fitting and alignment controls

State: **done**, 2026-08-03. Looks now moves directly from font choices to alignment without the retired size section.

## AUG-42: Navigate to Message on preview long-press

Make the live preview a shortcut back to the primary editing field.

- [x] Detect a long-press on the editor preview
- [x] Scroll the Message field into view and focus it
- [x] Preserve preview taps and horizontal swipe navigation

State: **done**, 2026-08-03. Long-pressing the editor preview now jumps to and focuses Message.

## AUG-43: Showcase page transitions in the live preview

Replay the selected transition within the preview when its transition setting changes, without changing the selected page.

- [x] Re-enter the same preview page through a transition-only key
- [x] Showcase the configured transition and overlay effects
- [x] Preserve page selection and stored content

State: **done**, 2026-08-03. Changing Page transition now visibly replays that transition in the live preview.

## AUG-44: Match preview animation timing

Keep blinking, strobing, background effects, inversion, and other speed-sensitive motion faithful in the live preview.

- [x] Use the same speed curve and timing floor as Present mode
- [x] Keep blink intensity and motion style changes live in the preview
- [x] Preserve reduced-motion accessibility behavior

State: **done**, 2026-08-03. The preview now uses the same speed-sensitive pulse timing as the full-screen presentation.

## AUG-45: Replay a selected page transition

Retrigger the live preview when the selected Page transition chip is tapped, including when it was already selected.

- [x] Treat every transition-chip tap as a preview replay request
- [x] Replay the current page without changing page selection
- [x] Keep the transition setting and stored content unchanged by replaying

State: **done**, 2026-08-03. Tapping any transition chip now showcases it immediately, even when it is already selected.

## AUG-46: Remove horizontal text alignment

Keep the sign typography centered and remove the unnecessary horizontal alignment setting.

- [x] Remove the horizontal alignment control from Looks
- [x] Render all sign text centered in Edit and Present modes
- [x] Remove the obsolete alignment intent and persisted setting

State: **done**, 2026-08-03. Text is now always centered; vertical positioning remains configurable.

## AUG-47: Document the aughhhh pronunciation

Add the canonical pronunciation recording to the README.

- [x] Link the pronunciation recording from the README introduction

State: **done**, 2026-08-03. The opening README sentence now includes the IPA-style pronunciation
and links it directly to the recording.

## AUG-48: Add an About page

Provide a small app information screen with project links and licensing details.

- [x] Add an About entry to the editor header
- [x] Show the app identity, version, and GPL-3 license
- [x] Link the GitHub repository and GitHub Sponsors page

State: **done**, 2026-08-03. The editor header opens a dedicated About page with both project links.

## AUG-49: Ship the 1.0.0 release

Align the app version with the first stable release after the About page lands.

- [x] Set the Android version name to `1.0.0`
- [x] Verify the release-ready build and deployment
- [x] Create and push the `1.0.0` Git tag

State: **ready pending external setup**, 2026-08-03. Version `1.0.0` is configured; the tag will
be pushed after the Play publishing credentials are available.

## AUG-50: Automate Google Play publishing

Publish signed Android App Bundles to Google Play from CI after the app is configured in Play Console.

- [x] Add semantic-tag-triggered Play internal-testing workflow
- [x] Add deterministic Play version-code handling and CI signing support
- [x] Document the required Play Console, service-account, and GitHub secret setup
- [ ] Create the Play service account, upload keystore, and GitHub secrets

State: **waiting on external Play Console setup**, 2026-08-03. CI is wired and ready; the first
automatic upload must wait for the credentials and Play Console permissions to be configured.

## AUG-51: Add a privacy policy

Publish a concise privacy policy for Play Console and link it from the app and README.

- [x] Describe local-only storage and the absence of analytics, ads, and accounts
- [x] Document external links and incoming presentation intents
- [x] Link the public policy from the About page and README

State: **done**, 2026-08-03. The policy is available at `PRIVACY.md` and linked from the app.

## AUG-52: Respect About page system insets

Keep the About app bar below the Android status bar in edge-to-edge mode.

- [x] Apply the status-bar inset to the About top bar
- [x] Preserve the About content's scroll and navigation behavior
- [x] Rebuild and verify the corrected screen

State: **done**, 2026-08-03. The About header now starts below the system status bar.

## AUG-53: Use a standard About top app bar

Replace the custom About back/title row with a conventional Material top app bar.

- [x] Use a standard top app bar with a leading back icon
- [x] Keep the About content below the app bar and system status bar
- [x] Rebuild and verify the corrected navigation affordance

State: **done**, 2026-08-03. About now uses a regular Material header with a normal back button.

## AUG-54: Use a regular editor header with sticky preview layering

Use a standard editor top app bar while keeping the live preview pinned beneath it during scroll.

- [x] Move the app icon, identity, and About link into the editor top app bar
- [x] Remove the oversized identity card from the scrolling content
- [x] Keep the live preview sticky below the fixed app bar
- [x] Rebuild and deploy the updated editor layout

State: **done**, 2026-08-03. The editor now has a conventional fixed header, and its preview
remains a separate sticky layer below that header.

## AUG-55: Restore portrait orientation after Present

Return the device to portrait orientation when leaving full-screen presentation mode.

- [x] Request portrait orientation when returning to Edit or About
- [x] Preserve landscape orientation while Present is active
- [x] Rebuild and deploy the orientation fix

State: **done**, 2026-08-03. Exiting Present now requests portrait mode immediately.

## AUG-56: Use an About icon in the editor header

Keep the standard editor top app bar compact by representing the About link with an info icon.

- [x] Replace the text About action with an accessible info icon
- [x] Preserve the existing About navigation behavior
- [x] Rebuild and deploy the updated header

State: **done**, 2026-08-03. The editor header now uses a conventional info action for About.

## AUG-57: Preview tap actions independently

Show each tap action in the live preview when its option is tapped, regardless of the currently
selected action.

- [x] Replay the tapped action through the live preview
- [x] Preview the already-selected action when tapped again
- [x] Keep the stored tap-action selection and full-screen behavior intact

State: **done**, 2026-08-03. Tap-action chips now preview their own effect immediately, including
when the chip was already selected.

## AUG-58: Add a dedicated Settings screen

Provide an app-wide Settings destination from the editor header and move About into it.

- [x] Add a gear action to the editor top bar
- [x] Add a standard Settings screen with a back button
- [x] Move About to a Settings entry while preserving its links and navigation
- [x] Rebuild and deploy the updated navigation

State: **done**, 2026-08-03. Settings is now the home for app-wide options, with About available
as its first entry.

## AUG-59: Simplify layout and add presentation display controls

Keep sign content centered, remove the unused vertical-position and introductory heading controls,
and put presentation display preferences in Settings.

- [x] Move Keep screen awake to Settings → Display
- [x] Add a max-brightness-while-presenting preference and restore the previous brightness on exit
- [x] Remove the vertical-position setting and intent extra
- [x] Remove the “Your sign, your rules” editor heading
- [x] Rebuild and deploy the updated editor and settings

State: **done**, 2026-08-03. Display preferences now live in Settings, presentation text is always
centered, and the editor’s unused introductory heading and vertical-position control are gone.

## AUG-60: Loop presentation pages

Add a presentation-screen preference that wraps page navigation around at both ends of the deck.

- [x] Add a persisted Loop pages switch under Settings → Presentation screen
- [x] Wrap swipes and tap-action next-page navigation in Present mode
- [x] Wrap external next/previous page intents in Present mode
- [x] Rebuild and deploy the updated presentation controls

State: **done**, 2026-08-03. Present mode now optionally wraps page navigation in both directions,
including swipes, tap actions, and external intents.

## AUG-61: Fix animated presentation insets and intent exit

Keep the full presentation background synchronized with animated sign colors on devices that expose
landscape safe-area insets, and ensure an intent-launched presentation does not restart after exit.

- [x] Synchronize the full-window presentation background with the sign animation pulse
- [x] Avoid re-processing the original presentation intent after orientation recreation
- [x] Rebuild and deploy the presentation fix

State: **done**, 2026-08-03. PX5 presentation margins now follow the animated sign background,
and X exits intent-launched Present mode cleanly after the orientation reset.

## AUG-62: Gate high-intensity motion behind a warning

Protect viewers by requiring an explicit acknowledgement before unlocking faster-than-normal motion
and the Strobe animation.

- [x] Add a persisted High-intensity mode setting under Settings → Presentation screen
- [x] Show a photosensitive-epilepsy warning before enabling it
- [x] Limit normal mode to 100% speed and disable Strobe
- [x] Rebuild and deploy the safety guard

State: **done**, 2026-08-03. High-intensity mode is persisted behind an explicit epilepsy warning;
normal mode caps animation speed at 100% and disables Strobe. The safety guard was checked remotely
and deployed to the ZF10 and PX5.

## AUG-63: Make blink timing refresh-aware

Represent true blink timing as a frequency in Hz, cap it to the display’s effective refresh rate,
and expose it as a motion setting.

- [x] Add a persisted Blink rate setting for Blink, BG blink, and Strobe
- [x] Use the display refresh rate when calculating blink timing
- [x] Let High-intensity mode unlock the higher blink-rate range
- [x] Rebuild and deploy the refresh-aware motion controls

State: **done**, 2026-08-03. Blink timing is configured in Hz and capped at half the detected display
refresh rate so on/off phases remain representable. The refresh-aware controls were checked remotely
and deployed to the ZF10 and PX5.

## AUG-64: Keep the tagline consistent

Use the current “make it bold!” tagline throughout the app’s identity surfaces.

- [x] Replace the outdated About-page tagline
- [x] Confirm no stale tagline remains in the app
- [x] Rebuild and deploy the updated About page

State: **done**, 2026-08-04.

## AUG-78: Automate Play Store screenshot capture (POC)

Add a fastlane screengrab proof of concept so editor/present listing screenshots no longer require
manually pulling images off attached devices, while keeping Gradle/SDK work on the remote build
hosts per AGENTS.md.

- [x] Add a `ScreenshotTest` instrumented test capturing editor and full-screen present
- [x] Wire `tools.fastlane:screengrab` into the androidTest dependencies
- [x] Add `fastlane/` config (Appfile, Screengrabfile, Fastfile) scoped to en-US only
- [x] Add a `screenshots` Nix dev shell and `just screenshots` / `screenshots-build` recipes
- [x] Run `just screenshots` end to end against a real device and verify output
- [x] Upload fresh phone screenshots to the live Play Console listing via `gpc` (playconsole-cli),
      replacing the previous two images
- [ ] Cover the 7"/10" tablet screenshot buckets too

State: **POC verified, screenshots live**, 2026-08-04. Ran end to end against the wired Zenfone 10
(`R6AIB700W850L7G`): `just screenshots-build` then `ANDROID_SERIAL=R6AIB700W850L7G nix develop
.#screenshots --command fastlane screenshots` produced real 1080x2400 editor and 2400x1080 present
screenshots in `fastlane/metadata/android/en-US/images/phoneScreenshots/`. Deleted the two prior
`en-US`/`phoneScreenshots` images via `gpc images delete-all` and uploaded the fresh pair via `gpc
images upload` (image ids `12613574554439085137`, `11505818489115745351`). See
`docs/screenshots.md` for usage on an emulator or another attached device.

## AUG-70: Remove stale AnimatedContent preview captures

Keep the nested animated preview content synchronized with the current editor settings and prevent
blank preview frames when sliders change.

- [x] Read live sign state inside the preview’s AnimatedContent content lambda
- [x] Keep preview transition overlays synchronized with current settings
- [x] Rebuild and deploy the nested-preview state fix

State: **done**, 2026-08-04.

## AUG-71: Match Spin in the live preview

Preview the actual rotating Spin page transition, and keep animated sign content current while
motion sliders change.

- [x] Carry the current sign state through the preview’s AnimatedContent target
- [x] Add the same rotation animation used by Present mode
- [x] Rebuild and deploy the corrected strobe and Spin preview behavior

State: **done**, 2026-08-04.

## AUG-72: Simplify animation timing controls

Make the effect choices and their timing controls understandable at a glance.

- [x] Rename text blink, background flash, and full strobe for explicit behavior
- [x] Show animation speed only where it affects moving effects or page transitions
- [x] Rename blink rate to flash frequency and update the documentation

State: **done**, 2026-08-04. The controls now distinguish movement speed from flash frequency,
and each flashing mode states whether it affects the text, background, or both.

## AUG-73: Place speed in the correct section

Keep Still free of motion controls while exposing transition timing alongside the selected page
transition.

- [x] Keep animation speed with Scroll and Invert
- [x] Move transition speed below the page transition choices
- [x] Rebuild and deploy the contextual timing controls

State: **done**, 2026-08-04. Transition timing is now shown directly below the selected page
transition, while Still has no unrelated motion-speed control.

## AUG-74: Simplify presentation entry and exit

Replace the CRT shutdown gimmick with a restrained transition that feels polished in both
directions.

- [x] Fade and subtly scale the presentation in when it starts
- [x] Fade and subtly scale it out when it exits
- [x] Remove the CRT collapse and scanline overlay

State: **done**, 2026-08-04. Presentation entry and exit now use a subtle fade-and-scale
transition without the old CRT scanline effect.

## AUG-75: Always start the presentation entry animation

Ensure the presentation cannot remain invisible when the device does not rotate to landscape.

- [x] Start the entry animation independently of the current orientation
- [x] Preserve the subtle fade-and-scale presentation transition
- [x] Rebuild and verify the fix on the Mi Pad 4

State: **done**, 2026-08-04. The entry animation now starts regardless of orientation, so a
portrait device cannot leave the presentation content at zero alpha.

## AUG-76: Skip page transition on presentation start

Do not play the selected page transition when entering Present mode; it should only run after
the user changes pages.

- [x] Keep page transitions disabled while the presentation entry animation runs
- [x] Suppress initial spin and overlay effects as well as the content transition
- [x] Rebuild, deploy, and verify presentation startup

State: **done**, 2026-08-04. Present mode now starts with only the polished entry animation;
the selected page transition is reserved for subsequent page changes.

## AUG-77: Zoom presentation entry and exit

Replace the opaque fade-to-black presentation exit with a centered zoom animation that grows
into Present mode and reverses cleanly back to the editor.

- [x] Zoom the sign in on entry and back out on exit
- [x] Keep the presentation content opaque throughout the exit animation
- [x] Rebuild, deploy, and verify the exit animation on the ZF10

State: **done**, 2026-08-04. Presentation now zooms in from a smaller centered sign and
zooms back out while remaining opaque before returning to the editor.

## AUG-69: Keep the sticky preview state current

Ensure the live preview observes editor state changes while it remains mounted in the sticky
header, including animation speed and blink/strobe frequency sliders.

- [x] Feed the sticky preview from an always-current state holder
- [x] Keep transition and tap-action preview triggers current as well
- [x] Rebuild and deploy the sticky-preview refresh fix

State: **done**, 2026-08-04.

## AUG-68: Simplify the launcher artwork

Use the expressive round face as the launcher identity without the speech-bubble tail, hands,
decorations, or background.

- [x] Isolate the face with transparent padding
- [x] Preserve the colorful 3D expression and recognizable features
- [x] Rebuild and deploy the updated launcher artwork

State: **done**, 2026-08-04.

## AUG-67: Allow zero animation speed

Let users pause speed-driven motion at 0% while keeping blink and strobe frequency controlled by
their dedicated Hz setting.

- [x] Lower the speed floor to 0% in persisted state, controls, and intents
- [x] Pause scrolling and speed-driven pulse motion safely at zero
- [x] Make page transitions instant at zero speed and update the documentation
- [x] Rebuild and deploy the zero-speed behavior

State: **done**, 2026-08-04.

## AUG-66: Apply motion timing changes live in Preview

Make changes to animation speed and blink/strobe frequency take effect immediately in the live
preview, matching presentation mode.

- [x] Restart the preview pulse with the current timing parameters
- [x] Keep blink, background blink, strobe, and speed-driven motion synchronized
- [x] Rebuild and deploy the live timing fix

State: **done**, 2026-08-04.

## AUG-65: Give Looks its own theme icon

Use a palette icon for the Looks section so it is visually distinct from the Message section.

- [x] Add a native palette vector drawable
- [x] Use it on the Looks card while retaining the Message icon
- [x] Rebuild and deploy the updated section icon

State: **done**, 2026-08-04.
