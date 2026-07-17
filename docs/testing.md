# Testing

## Design-system screenshot tests

`core:designsystem` uses Android's host-side Compose Preview Screenshot Testing. The golden sheet covers Momentum Athletic components in light mode, dark mode, and at a 1.5 font scale on a 390 × 884 dp canvas.

Generate or intentionally update references after reviewing a visual change:

```bash
./gradlew :core:designsystem:updateDebugScreenshotTest
```

Validate the checked-in references without changing them:

```bash
./gradlew :core:designsystem:validateDebugScreenshotTest
```

The HTML diff report is written to `core/designsystem/build/reports/screenshotTest/preview/debug/index.html`. Reference PNGs live in `core/designsystem/src/screenshotTestDebug/reference/` and must be committed with intentional visual changes.

## Local verification

For design-system changes, run:

```bash
./gradlew help
./gradlew :core:designsystem:validateDebugScreenshotTest
./gradlew :app:assembleDebug
```

For integration milestones, use the full project checks:

```bash
./gradlew test assembleDebug lint
```

Connected UI tests and physical-device camera checks remain required before release when a device is available.
