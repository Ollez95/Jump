# Jump architecture

Jump follows the same broad architecture used by **Now in Android**: unidirectional data flow,
repositories as the public data API, screen-level feature modules, Hilt dependency injection, and
an app module that composes features and owns root navigation.

## Module graph

```text
:app
  ├── :feature:{onboarding,home,workout,history,settings}
  ├── :core:designsystem
  └── :core:workout

:feature:* → :core:data / :core:domain / :core:model / :core:designsystem
:core:workout → :core:data → :core:database + :core:datastore
:core:domain → :core:model
```

- `:app` is the Android entry point and owns Navigation 3, runtime permission orchestration, and
  startup state. It contains no persistence or workout implementation.
- `:feature:*` modules own their routes, stateless screens, UI state, and Hilt ViewModels.
- `:core:model` contains platform-independent domain models.
- `:core:domain` contains reusable business rules such as adaptive workout planning.
- `:core:data` exposes repository interfaces and binds their default implementations.
- `:core:database` and `:core:datastore` are implementation details behind repositories.
- `:core:workout` owns the sensor detector, session coordinator, controller, and foreground service.
- `:core:designsystem` owns the theme, reusable components, and display formatting.

## Dependency rules

- Features never depend on other features; the app mediates navigation with primitive IDs.
- Features depend on repository abstractions, not Room DAOs or DataStore.
- Implementation modules are not exposed transitively with `api` dependencies.
- Hilt scopes stateful infrastructure to the application and ViewModels to navigation entries.
- Navigation entries receive saveable-state and ViewModel-store decorators so feature ViewModels are
  cleared when their destination is popped.
- Shared Android and JVM settings live in convention plugins under `build-logic`.
