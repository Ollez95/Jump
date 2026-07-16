# `:core:datastore`

Preferences DataStore access for user configuration that does not belong in the workout-history database.

## Owns

`JumpPreferencesDataSource` exposes reactive flows and updates for:

- onboarding completion, experience, training goal, and weekly frequency;
- voice, tone, and vibration coaching cues;
- the default motion or camera counting mode;
- custom jump duration, rest duration, and round count.

Unknown enum values fall back to safe defaults, I/O read failures emit empty preferences, and custom interval values are normalized before exposure or storage.

## Dependencies

- `:core:model`
- AndroidX Preferences DataStore
- Hilt

## Boundaries

Features use `UserPreferencesRepository` from `:core:domain`; only the `:core:data` implementation should expose this data source to the rest of the app.

## Verification

```bash
./gradlew :core:datastore:testDebugUnitTest
```

[Back to the module catalog](../../README.md#modules)

