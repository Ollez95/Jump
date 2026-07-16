# `:feature:settings`

User-facing configuration for counting, coaching cues, and the adaptive training profile.

## Experience

- Select motion or camera as the default counting method.
- Enable or disable voice coaching, transition tones, and vibration.
- Review current experience, training goal, and weekly frequency.
- Reset onboarding to change the training profile.
- Read privacy and placement guidance for each counting method.

## Public surface

- `SettingsRoute` collects profile, cue, and counting-mode flows.
- `SettingsScreen` is callback-driven UI.
- `SettingsViewModel` delegates updates to `UserPreferencesRepository`.

## Dependencies

- `:core:model`, `:core:domain`, and `:core:designsystem`
- Compose, Lifecycle, and Hilt

The feature does not know about DataStore keys or permission-dialog behavior.

## Verification

```bash
./gradlew :feature:settings:testDebugUnitTest
```

[Back to the module catalog](../../README.md#modules)

