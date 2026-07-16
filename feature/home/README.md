# `:feature:home`

The Today dashboard and primary workout-launch surface.

## Experience

- Shows weekly goal progress and the best corrected jump count.
- Builds an adaptive daily plan from profile and recent sessions.
- Offers quick, daily, and custom interval entry points.
- Lets the athlete select motion or camera counting.
- Displays an in-progress workout and continues it without starting a duplicate session.
- Estimates calories for the saved custom workout configuration.
- Explains a failed start when the selected counting mode lacks permission.

## Public surface

- `HomeRoute` collects lifecycle-aware state and connects navigation callbacks.
- `HomeScreen` renders immutable `HomeUiState`.
- `HomeViewModel` combines preferences, history, active workout state, planning, and calorie estimates.

## Dependencies

- `:core:model`, `:core:domain`, `:core:workout`, and `:core:designsystem`
- Compose, Lifecycle, and Hilt

The module requests starts through callbacks. Permission dialogs and destination changes remain owned by `:app`.

## Verification

```bash
./gradlew :feature:home:testDebugUnitTest
```

[Back to the module catalog](../../README.md#modules)

