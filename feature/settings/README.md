# `:feature:settings` (Profile)

User-facing configuration for counting, coaching cues, and the adaptive training profile.

## Experience

- Select motion or camera as the default counting method.
- Enable or disable voice coaching, transition tones, and vibration.
- Review current experience, training goal, and weekly frequency in a profile summary.
- Open the dedicated training-profile editor without returning to onboarding.
- Read privacy and placement guidance for each counting method.

## Public surface

- `ProfileRoute` collects profile, cue, and counting-mode flows for the bottom tab.
- `TrainingProfileRoute` hosts the dedicated profile editor.
- `ProfileScreen` and `TrainingProfileScreen` are callback-driven UI.
- Both screen composables include paired light/dark Compose previews.
- `ProfileViewModel` delegates updates to `UserPreferencesRepository`.

## Dependencies

- `:core:model`, `:core:domain`, and `:core:designsystem`
- Compose, Lifecycle, and Hilt

The feature does not know about DataStore keys or permission-dialog behavior.

## Verification

```bash
./gradlew :feature:settings:testDebugUnitTest
```

[Back to the module catalog](../../README.md#modules)
