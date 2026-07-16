# `:feature:workoutsetup`

Custom interval-workout builder for athletes who want direct control over work, recovery, and rounds.

## Experience

- Adjust jump duration, rest duration, and round count within model-defined limits.
- Choose motion or camera counting for the workout.
- Preview total time, active time, and estimated calorie range.
- Reuse the last saved custom configuration.
- Prevent a new start while another workout is active.

`WorkoutSetupViewModel` normalizes and persists each change through `UserPreferencesRepository`. `IntervalWorkoutPlanner` turns the final configuration into the same `WorkoutPlan` consumed by the shared workout runtime.

## Public surface

- `WorkoutSetupRoute` connects state, start, and back-navigation callbacks.
- `WorkoutSetupScreen` renders `WorkoutSetupUiState`.
- `WorkoutSetupViewModel` coordinates preferences, planning, estimates, and active state.

## Dependencies

- `:core:model`, `:core:domain`, `:core:designsystem`, and `:core:workout`
- Compose, Lifecycle, and Hilt

## Verification

```bash
./gradlew :feature:workoutsetup:testDebugUnitTest
```

[Back to the module catalog](../../README.md#modules)

