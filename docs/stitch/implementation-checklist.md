# Stitch implementation checklist

Use this checklist for every screen and milestone.

## Content

- [ ] Existing workout, profile, history, calorie, analytics, and settings data remains available.
- [ ] New XP, level, streak, quest, and achievement values have deterministic sources.
- [ ] Guest mode remains fully useful offline.
- [ ] User-visible copy is stored in Android string resources.
- [ ] Units, dates, numbers, and plurals are formatted consistently.

## Interaction

- [ ] Primary action and back behavior match the existing navigation contract.
- [ ] Touch targets are at least 48 × 48 dp.
- [ ] Buttons expose disabled states without removing surrounding content.
- [ ] State restoration covers selection, scroll, timers, and dialogs.
- [ ] Destructive actions require confirmation and remain in detail contexts.

## Loading, empty, and error

- [ ] Refresh/loading keeps the stable layout visible.
- [ ] Empty states explain the next useful action.
- [ ] Permission and camera failures offer a recovery action.
- [ ] Offline account/backup failures never block local workouts.
- [ ] Errors do not discard unsaved workout or profile input.

## Theme and adaptability

- [ ] Light and dark previews are present.
- [ ] Screen is checked at 390 × 884 dp.
- [ ] Screen scrolls at 1.5× font scale without clipping.
- [ ] Larger phone width does not stretch content excessively.
- [ ] System bars, gesture navigation, and IME insets are respected.

## Accessibility

- [ ] TalkBack labels describe every interactive icon.
- [ ] Dynamic counters and progress have semantic equivalents.
- [ ] Contrast meets WCAG AA for functional text and controls.
- [ ] Selection does not rely on color alone.
- [ ] Decorative images and pose skeletons are excluded from semantics.

## Verification

- [ ] Business rules have local unit tests.
- [ ] Screen behavior has Compose UI tests.
- [ ] Screenshot coverage includes light, dark, and 1.5× font scale.
- [ ] Relevant module tests pass.
- [ ] `./gradlew test assembleDebug lint` passes.
- [ ] Connected UI tests pass on an available device.
- [ ] Camera tracking is exercised on a physical device.
