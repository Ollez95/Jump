# `:app`

The Android application shell. It assembles every feature, owns process and Activity entry points, and coordinates root navigation and permission requests.

## Responsibilities

- Initialize Hilt through `JumpApplication` and host Compose in `MainActivity`.
- Apply `JumpTheme` and choose between loading, onboarding, and the main experience.
- Own the Navigation 3 back stack and destinations for Today, custom setup, active workout, history, progress, settings, and session details.
- Request runtime permissions through the Activity Result API before starting a workout.
- Bridge navigation events to `WorkoutController` and `WorkoutCoordinator` through `AppViewModel`.

## Main entry points

- `JumpApplication` — application-level Hilt component.
- `MainActivity` — edge-to-edge Compose host.
- `JumpApp` — startup gate based on the stored profile.
- `MainNavigation` — root Navigation 3 graph and bottom navigation.
- `AppViewModel` — application-level workout-start and permission orchestration.

## Dependencies

The app depends on all seven feature modules and the core modules required for models, domain contracts, repositories, UI, workout execution, and permission checks. It does not implement persistence or jump detection itself.

## Verification

```bash
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
```

Connected UI checks can be run with `./gradlew :app:connectedDebugAndroidTest` when a device is available.

[Back to the project README](../README.md)

