# Momentum Athletic design system

Momentum Athletic is a premium, focused fitness language built around calm readiness and steady progression. It evolves Material 3 rather than replacing it.

## Principles

- Use edge-to-edge layouts with a strict 20 dp content margin.
- Keep an 8 dp baseline grid with 8, 16, 24, 32, and 64 dp vertical rhythm.
- Create depth with tonal surface containers; reserve shadows for floating overlays.
- Use large athletic display typography and generous whitespace instead of dense decoration.
- Keep workout counting, saving, camera, and navigation behavior independent of presentation.

## Typography

| Role | Family | Size | Weight | Line height |
|---|---|---:|---:|---:|
| Display large | Anybody | 56 sp | 800 | 64 sp |
| Display mobile | Anybody | 40 sp | 800 | 48 sp |
| Headline large | Anybody | 32 sp | 700 | 40 sp |
| Headline medium | Anybody | 24 sp | 700 | 32 sp |
| Title large | Lexend | 20 sp | 600 | 28 sp |
| Body large | Lexend | 16 sp | 400 | 24 sp |
| Body medium | Lexend | 14 sp | 400 | 20 sp |
| Label large | Lexend | 12 sp | 600 | 16 sp |

Anybody is used for displays and headings. Lexend is used for functional text, labels, values, and controls.

## Light color roles

| Role | Value |
|---|---|
| Background / surface | `#EEFDF4` |
| Surface container lowest | `#FFFFFF` |
| Surface container low | `#E9F7EE` |
| Surface container | `#E3F1E8` |
| Surface container high | `#DDECE3` |
| Surface container highest | `#D7E6DD` |
| On surface | `#121E19` |
| On surface variant | `#3F4943` |
| Outline | `#6F7A72` |
| Outline variant | `#BEC9C1` |
| Primary | `#005037` |
| On primary | `#FFFFFF` |
| Primary container | `#006B4A` |
| On primary container | `#92E8BF` |
| Secondary | `#276294` |
| Secondary container | `#91C5FD` |
| Tertiary / momentum | `#643E00` |
| Tertiary container | `#845300` |
| Momentum fixed dim | `#FFB95F` |
| Error | `#BA1A1A` |
| Error container | `#FFDAD6` |

Dark mode uses the same semantic roles, with a near-black green base (`#030A07`), electric mint primary actions, and tonal containers. Components must never embed raw light-mode values.

## Shape and spacing tokens

- Small: 4 dp
- Default: 8 dp
- Medium: 12 dp
- Large: 16 dp
- Extra large: 24 dp
- Full: pill/circle
- Screen margin: 20 dp
- Gutter: 16 dp
- Section spacing: 32 dp

## Components

- Primary workout actions: 56 dp tall, 12 dp corners, filled primary.
- Secondary actions: tonal or outlined, 12 dp corners.
- Momentum actions: amber container with ink content; only for XP, streak, quest, and achievement actions.
- Training cards: 24 dp corners, lowest surface, 20 dp padding.
- Metric cards: 18 dp corners, subtle mint outline, large display values.
- Progress: pill tracks and rounded caps.
- Quest chips: blue for in-progress, amber for claimable/completed.
- Lists: 72 dp minimum rows, 20 dp horizontal alignment, minimal 20 dp navigation glyphs.
- Inputs: 12 dp corners, tonal fill, 2 dp primary focus indication.

## Accessibility

- Minimum interactive target: 48 × 48 dp.
- Text and icons use paired Material color roles.
- Dynamic workout values expose concise TalkBack descriptions and live-region behavior only where useful.
- Decorative imagery and pose overlays are hidden from accessibility services.
- Progress visuals expose semantic progress ranges and a text equivalent.
