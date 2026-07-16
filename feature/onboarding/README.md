# `:feature:onboarding`

First-run setup that turns a few training choices into the profile used by Jump's adaptive planner.

## Experience

The screen asks for:

- experience level: beginner, regular, or advanced;
- primary goal: consistency, endurance, or speed;
- desired training frequency from two to six sessions per week.

`OnboardingViewModel` saves the completed `UserProfile` through `UserPreferencesRepository`. The application shell observes that profile and automatically replaces onboarding with the main navigation when saving completes.

## Public surface

- `OnboardingRoute` wires the Hilt ViewModel to UI.
- `OnboardingScreen` is a stateless, callback-driven composable suitable for previews and UI tests.
- `OnboardingViewModel` performs the persistence action.

## Dependencies

- `:core:model`
- `:core:domain`
- `:core:designsystem`
- Compose, Lifecycle ViewModel, and Hilt

The feature does not navigate directly and never accesses DataStore.

## Verification

```bash
./gradlew :feature:onboarding:testDebugUnitTest
```

[Back to the module catalog](../../README.md#modules)

