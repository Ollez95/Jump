# `:core:workout`

The shared workout runtime. It owns live session state, jump counting from motion events, work/rest timing, foreground execution, coaching cues, and final persistence.

## Owns

- `WorkoutCoordinator` — the active-session state machine, pace/streak metrics, pause/resume, correction, and idempotent save path.
- `WorkoutController` — application-facing commands that start or control `WorkoutService`.
- `WorkoutService` — foreground lifecycle, sensor events, timer ticks, notification, text-to-speech, tones, and vibration.
- `JumpDetector` — filtered motion-sensor jump detection.
- Coroutine dispatcher bindings for service and persistence work.

Camera detections enter the same `WorkoutCoordinator`, so both counting methods share intervals, metrics, completion behavior, and Room history.

## Dependencies

- `:core:model`
- `:core:domain`
- AndroidX Core and Lifecycle Service
- Hilt and coroutines

Its manifest contributes activity-recognition, foreground-service, notification, and vibration permissions plus the non-exported health foreground service.

## Tests

```bash
./gradlew :core:workout:testDebugUnitTest
```

Local tests cover jump filtering and coordinator state/persistence behavior.

[Back to the module catalog](../../README.md#modules)

