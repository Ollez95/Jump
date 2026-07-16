# `:core:database`

Room-backed storage for workout history and interval details.

## Owns

- `JumpDatabase`, currently schema version 1.
- `WorkoutSessionEntity` and `WorkoutIntervalEntity` persistence models.
- `WorkoutDao` queries for observing, reading, inserting, correcting, and deleting sessions.
- `DatabaseModule`, which provides the application-scoped database and DAO through Hilt.
- Exported Room schema snapshots under `schemas/`.

Sessions and their ordered intervals use a parent/child layout. Repository code is responsible for coordinating multi-table reads and writes and converting entities to domain models.

## Dependencies

- `:core:model`
- Room runtime/KTX and compiler
- Hilt

## Boundaries

Features must not depend on this module. They consume `WorkoutRepository` from `:core:domain`; `:core:data` is the only layer that should coordinate DAO access for the app.

## Verification

```bash
./gradlew :core:database:testDebugUnitTest
```

When the schema changes, increment the database version, provide the required migration strategy, and commit the new JSON schema snapshot.

[Back to the module catalog](../../README.md#modules)

