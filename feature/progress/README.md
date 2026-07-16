# `:feature:progress`

Training insights derived from the complete workout history.

## Experience

- Current and longest training-day streaks.
- A 13-week activity calendar with accessible activity-level descriptions.
- An eight-week activity chart.
- Total sessions, training days, jumps, active time, and estimated calories.
- A helpful empty state before the first training session.

## Public surface

- `ProgressRoute` collects analyzed state and connects back navigation.
- `ProgressScreen` renders `ProgressUiState`.
- `ProgressViewModel` maps repository history through `WorkoutProgressAnalyzer`.

## Dependencies

- `:core:model`, `:core:domain`, and `:core:designsystem`
- Compose, Lifecycle, and Hilt

Calendar and streak rules belong to `:core:domain`; this module focuses on visualization and accessibility.

## Verification

```bash
./gradlew :feature:progress:testDebugUnitTest
```

Core analysis behavior is tested in `:core:domain`.

[Back to the module catalog](../../README.md#modules)

