# `:core:permissions`

Centralized permission policy for starting motion- or camera-counted workouts.

## Owns

- `WorkoutPermissionManager`, the public API used by `:app` to query missing permissions and validate a counting mode.
- `WorkoutPermissionPolicy`, which selects camera, activity-recognition, notification, and foreground-service permissions for the current SDK.
- The Hilt binding for the Android implementation.

The module checks grant state only. It does not show system dialogs or UI; `:app` owns the Activity Result launcher and decides how navigation responds.

## Dependencies

- `:core:model` for `CountingMode`
- AndroidX Core
- Hilt

## Tests

```bash
./gradlew :core:permissions:testDebugUnitTest
```

Policy tests cover SDK-specific permission sets and prevent request behavior from being scattered across screens.

[Back to the module catalog](../../README.md#modules)

