# `:core:designsystem`

Jump's shared visual language: Material 3 theme, design tokens, navigation chrome, reusable Compose components, and display formatters.

## Owns

- `JumpTheme`, Momentum Athletic light/dark color schemes, bundled Anybody/Lexend typography, shapes, and `JumpSpacing` tokens.
- Screen structure and navigation: `JumpScreen`, `JumpTopAppBar`, `JumpNavigationBar`, and navigation item/icon models.
- Reusable cards, buttons, selectors, settings rows, progress, statistics, banners, empty states, brand elements, and reward/quest surfaces.
- `formatDuration` and `formatDate` presentation helpers.

## Dependencies

This Android Compose library depends on the Compose BOM, UI, Material 3, preview tooling, and debug tooling. It deliberately has no feature or persistence dependencies.

## Boundaries

Keep components generic and driven by parameters. Screen-specific state and business logic stay in feature modules; domain calculations stay in `:core:domain`.

## Verification

```bash
./gradlew :core:designsystem:assembleDebug
./gradlew :core:designsystem:validateDebugScreenshotTest
```

See [the screenshot-testing guide](../../docs/testing.md) for updating reviewed golden images.

[Back to the module catalog](../../README.md#modules)
