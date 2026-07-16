# `:feature:history`

Workout history, session details, jump correction, and deletion.

## Experience

- Observe saved sessions in newest-first order.
- See duration, jump count, average pace, status, and calorie estimate at a glance.
- Open a detailed record with timing, pace, streak, and interval information.
- Correct the final jump count after a workout.
- Delete from the list or detail screen with confirmation.
- Open the progress dashboard from history.

## Public surface

- `HistoryRoute` / `HistoryScreen` render the session list.
- `SessionDetailRoute` / `SessionDetailScreen` load one session by its navigation ID.
- `HistoryViewModel` and `SessionDetailViewModel` use `WorkoutRepository` and `WorkoutCalorieEstimator`.

## Dependencies

- `:core:model`, `:core:domain`, and `:core:designsystem`
- Compose, Lifecycle, and Hilt

Only the primitive session ID crosses the navigation boundary; this feature never reads Room directly.

## Verification

```bash
./gradlew :feature:history:testDebugUnitTest
```

[Back to the module catalog](../../README.md#modules)

