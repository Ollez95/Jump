# `:core:designsystem`

Jump's shared visual language: Material 3 theme, design tokens, navigation chrome, reusable Compose components, and display formatters.

## Owns

- `JumpTheme`, light/dark color schemes, typography, shapes, and `JumpSpacing` tokens.
- Screen structure and navigation: `JumpScreen`, `JumpTopAppBar`, `JumpNavigationBar`, and navigation item/icon models.
- Reusable cards, buttons, selectors, settings rows, progress, statistics, banners, empty states, and brand elements.
- `formatDuration` and `formatDate` presentation helpers.

## Dependencies

This Android Compose library depends on the Compose BOM, UI, Material 3, preview tooling, and debug tooling. It deliberately has no feature or persistence dependencies.

## Boundaries

Keep components generic and driven by parameters. Screen-specific state and business logic stay in feature modules; domain calculations stay in `:core:domain`.

## Verification

```bash
./gradlew :core:designsystem:assembleDebug
```

[Back to the module catalog](../../README.md#modules)

