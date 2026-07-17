# Account foundation

Provider-neutral account contracts and the offline-first default implementation.

The persisted `LocalUserId` is the durable owner of local workouts, settings, and
rewards. Signing in associates a provider account with that local identity; it
does not replace the local identity. Signing out returns to the same guest
identity, so the app remains fully functional offline.

No authentication or cloud SDK is selected here. The default gateway, backup,
and sync implementations return explicit offline-only errors. A future backend
adapter can replace those Hilt bindings without changing feature screens.

Guest data is merged per category:

- workouts: union by stable workout ID, keeping both versions when IDs differ;
- settings: local guest settings win when present;
- rewards: keep the greatest earned progress and never add XP twice.

The merge models describe plans and execution results. Actual data adapters are
deliberately left to the integration layer because this module does not own or
alter the existing workout database schema.
