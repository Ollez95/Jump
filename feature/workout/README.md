# `:feature:workout`

The live workout and completion UI shared by motion- and camera-counted sessions.

## Experience

- Shows preparation, active, rest, paused, and completed phases.
- Presents jump count, time, pace, streak, interval progress, and sensor status.
- Pauses/resumes or finishes through `WorkoutController`.
- Embeds camera preview and tracking feedback only for camera mode.
- Forwards camera jump events into the shared `WorkoutCoordinator`.
- Allows a final jump-count correction before leaving the completion screen.

## Public surface

- `WorkoutRoute` collects the active `StateFlow` and clears completed state on exit.
- `WorkoutScreen` renders `ActiveWorkoutState` and exposes callback-driven controls.
- `WorkoutViewModel` bridges UI commands to the workout controller/coordinator.

## Dependencies

- `:core:model`, `:core:workout`, `:core:camera`, and `:core:designsystem`
- Compose, Lifecycle, and Hilt

Timing, sensors, notification, and persistence stay outside the feature in `:core:workout`.

## Tests

```bash
./gradlew :feature:workout:testDebugUnitTest
./gradlew :feature:workout:connectedDebugAndroidTest
```

The connected Compose test covers the workout screen's core interactive state. It requires an emulator or device.

[Back to the module catalog](../../README.md#modules)

