# Jump architecture

Jump follows the same broad architecture used by **Now in Android**: unidirectional data flow,
repositories as the public data API, screen-level feature modules, Hilt dependency injection, and
an app module that composes features and owns root navigation.

## Module graph

```text
:app
  ├── :feature:{onboarding,home,workout,workoutsetup,history,progress,settings}
  ├── :core:designsystem
  ├── :core:workout
  └── :core:camera

:feature:* → :core:domain / :core:model / :core:designsystem
:core:workout → :core:domain
:core:data → :core:domain + :core:database + :core:datastore
:core:domain → :core:model
```

- `:app` is the Android entry point and owns Navigation 3, runtime permission orchestration, and
  startup state. It contains no persistence or workout implementation.
- `:feature:*` modules own their routes, stateless screens, UI state, and Hilt ViewModels.
- `:core:model` contains platform-independent domain models.
- `:core:domain` contains repository contracts and reusable business rules such as adaptive workout planning.
- `:core:data` implements repository contracts and binds the implementations with Hilt.
- `:core:database` and `:core:datastore` are implementation details behind repositories.
- `:core:workout` owns the sensor detector, session coordinator, controller, and foreground service.
- `:core:camera` owns lifecycle-bound preview, on-device pose analysis, and camera jump detection.
- `:core:designsystem` owns the theme, reusable components, and display formatting.

## Configurable interval workouts

`:feature:workoutsetup` owns the interval builder UI and screen state. Jump time, rest time, and
round count are persisted through `UserPreferencesRepository`; the feature never accesses
DataStore directly. `IntervalWorkoutPlanner` validates the configuration and converts it to the
same ordered `WorkoutInterval` list used by adaptive daily plans.

Custom plans therefore run through `WorkoutCoordinator` and `WorkoutService` without a second
timer implementation. They inherit pause/resume, work/rest cues, foreground execution, camera or
motion counting, metrics, completion, and Room-backed history persistence.

## Dependency rules

- Features never depend on other features; the app mediates navigation with primitive IDs.
- Features depend on repository abstractions in `:core:domain`, not the `:core:data` implementation,
  Room DAOs, or DataStore.
- Implementation modules are not exposed transitively with `api` dependencies.
- Hilt scopes stateful infrastructure to the application and ViewModels to navigation entries.
- Repository contracts live in `:core:domain`; only `:core:data` knows the Room and DataStore
  implementations.
- Workout completion is idempotent, so concurrent UI/service finish requests persist one session.
- Long-running workout work uses injected coroutine dispatchers; service teardown never blocks the
  main thread while Room writes complete.
- Navigation entries receive saveable-state and ViewModel-store decorators so feature ViewModels are
  cleared when their destination is popped.
- Shared Android and JVM settings live in convention plugins under `build-logic`.

## Camera counting

Camera counting is an optional alternative to pocket motion counting. `:core:camera` binds CameraX
preview and image analysis to the workout screen lifecycle, uses ML Kit Pose Detection in streaming
mode, and converts shoulder/hip landmarks into normalized vertical body-position samples. A pure
detector applies calibration, smoothing, confidence thresholds, hysteresis, air-time limits, and a
cooldown before emitting a jump on landing.

Frames are processed in memory with `STRATEGY_KEEP_ONLY_LATEST`, closed immediately after inference,
and are never recorded, persisted, or sent by the app. The camera is released when the workout
screen leaves the active lifecycle. Camera detections enter `WorkoutCoordinator`, so timing,
pause/rest behavior, metrics, corrections, and session persistence stay identical across counting
methods.
