# `:core:domain`

Pure Kotlin business rules and repository contracts. This module describes what Jump can do without knowing how Android persistence or screens implement it.

## Owns

- `WorkoutRepository` and `UserPreferencesRepository` contracts.
- `AdaptiveWorkoutPlanner` for experience-, goal-, and history-aware daily plans.
- `IntervalWorkoutPlanner` for validated custom work/rest plans.
- `WorkoutCalorieEstimator` for active-time calorie ranges.
- `WorkoutProgressAnalyzer` and its calendar, weekly, streak, and aggregate report models.

## Dependencies

- `:core:model`
- `javax.inject` for constructor injection metadata
- Kotlin coroutines for `Flow`-based repository contracts

It has no Android framework dependency. Repository implementations live in `:core:data` so domain logic stays unit-testable.

## Tests

Planner, calorie, and progress analysis behavior is covered with local JUnit tests.

```bash
./gradlew :core:domain:test
```

[Back to the module catalog](../../README.md#modules)

