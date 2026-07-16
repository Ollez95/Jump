# `:core:model`

Platform-independent Kotlin models shared across Jump's domain, data, workout, permissions, and UI layers.

## Owns

- Training profile and preferences: `UserProfile`, `ExperienceLevel`, `TrainingGoal`, `CuePreferences`, and `CountingMode`.
- Workout definitions: `WorkoutPlan`, `WorkoutInterval`, `IntervalWorkoutConfig`, `WorkoutKind`, and `IntervalType`.
- Recorded results: `WorkoutSession`, `JumpMetrics`, and `SessionStatus`.
- Live state: `ActiveWorkoutState` and `SessionPhase`.

Models contain values and small invariants only. For example, `IntervalWorkoutConfig.normalized()` constrains custom workout input and `WorkoutPlan.durationSeconds` derives total duration.

## Dependencies

This is a Kotlin/JVM module. It has no Android dependency; coroutines are available for model-adjacent shared APIs.

## Boundaries

Business decisions belong in `:core:domain`, persistence representations in `:core:database`, and presentation formatting in `:core:designsystem` or a feature.

## Verification

```bash
./gradlew :core:model:test
```

[Back to the module catalog](../../README.md#modules)

