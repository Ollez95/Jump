# `:core:camera`

Optional front-camera jump counting implemented with CameraX and on-device ML Kit pose detection.

## Owns

- `CameraJumpPreview`, which binds preview and keep-latest image analysis to a lifecycle owner.
- Pose-to-frame conversion using shoulder and hip landmarks.
- `CameraJumpDetector`, a pure stateful detector with calibration, confidence checks, smoothing, hysteresis, air-time validation, and cooldown.
- `CameraPoseFrame`, `CameraJumpResult`, and `CameraTrackingState` camera-specific models.

Frames are processed in memory, closed after inference, and are not recorded or persisted by this module. The manifest declares camera access and marks the front camera as optional hardware.

## Dependencies

- Jetpack Compose
- CameraX core, Camera2, lifecycle, and view artifacts
- ML Kit Pose Detection

The module intentionally does not depend on workout state or persistence. `:feature:workout` forwards emitted jumps to `WorkoutCoordinator`.

## Tests

```bash
./gradlew :core:camera:testDebugUnitTest
```

Detector tests exercise calibration, pose confidence, jump phases, false-positive rejection, and cooldown behavior without a camera device.

[Back to the module catalog](../../README.md#modules)

