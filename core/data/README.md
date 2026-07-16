# `:core:data`

The data-layer implementation that connects domain repository contracts to Room and DataStore.

## Owns

- `DefaultWorkoutRepository`, which observes history, saves sessions and intervals, loads details, corrects counts, and deletes sessions.
- `DefaultUserPreferencesRepository`, which exposes profile and setting flows and delegates updates to DataStore.
- Entity/domain mappings for workout sessions and interval rows.
- `DataModule`, which binds both implementations to their `:core:domain` interfaces with Hilt.

## Dependencies

- `:core:model`
- `:core:domain`
- `:core:database`
- `:core:datastore`
- Room KTX, coroutines, and Hilt

## Boundaries

This module is an implementation detail assembled by `:app`. Feature modules should depend on repository interfaces in `:core:domain`, never repository implementations here.

## Verification

```bash
./gradlew :core:data:testDebugUnitTest
```

[Back to the module catalog](../../README.md#modules)

