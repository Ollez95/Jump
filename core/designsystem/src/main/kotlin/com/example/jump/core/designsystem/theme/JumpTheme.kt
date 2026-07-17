package com.example.jump.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.jump.core.designsystem.R

private val MomentumLightColors = lightColorScheme(
  primary = Color(0xFF005037),
  onPrimary = Color(0xFFFFFFFF),
  primaryContainer = Color(0xFF006B4A),
  onPrimaryContainer = Color(0xFF92E8BF),
  inversePrimary = Color(0xFF82D8AF),
  secondary = Color(0xFF276294),
  onSecondary = Color(0xFFFFFFFF),
  secondaryContainer = Color(0xFF91C5FD),
  onSecondaryContainer = Color(0xFF0D5283),
  tertiary = Color(0xFF643E00),
  onTertiary = Color(0xFFFFFFFF),
  tertiaryContainer = Color(0xFF845300),
  onTertiaryContainer = Color(0xFFFFCF98),
  background = Color(0xFFEEFDF4),
  onBackground = Color(0xFF121E19),
  surface = Color(0xFFEEFDF4),
  onSurface = Color(0xFF121E19),
  surfaceVariant = Color(0xFFD7E6DD),
  onSurfaceVariant = Color(0xFF3F4943),
  surfaceTint = Color(0xFF026C4B),
  inverseSurface = Color(0xFF27332D),
  inverseOnSurface = Color(0xFFE6F4EB),
  surfaceDim = Color(0xFFCFDDD5),
  surfaceBright = Color(0xFFEEFDF4),
  surfaceContainerLowest = Color(0xFFFFFFFF),
  surfaceContainerLow = Color(0xFFE9F7EE),
  surfaceContainer = Color(0xFFE3F1E8),
  surfaceContainerHigh = Color(0xFFDDECE3),
  surfaceContainerHighest = Color(0xFFD7E6DD),
  outline = Color(0xFF6F7A72),
  outlineVariant = Color(0xFFBEC9C1),
  error = Color(0xFFBA1A1A),
  onError = Color(0xFFFFFFFF),
  errorContainer = Color(0xFFFFDAD6),
  onErrorContainer = Color(0xFF93000A),
  scrim = Color(0xFF000000),
  primaryFixed = Color(0xFF9EF4CA),
  primaryFixedDim = Color(0xFF82D8AF),
  onPrimaryFixed = Color(0xFF002114),
  onPrimaryFixedVariant = Color(0xFF005137),
  secondaryFixed = Color(0xFFD0E4FF),
  secondaryFixedDim = Color(0xFF9ACBFF),
  onSecondaryFixed = Color(0xFF001D34),
  onSecondaryFixedVariant = Color(0xFF004A79),
  tertiaryFixed = Color(0xFFFFDDB8),
  tertiaryFixedDim = Color(0xFFFFB95F),
  onTertiaryFixed = Color(0xFF2A1700),
  onTertiaryFixedVariant = Color(0xFF653E00),
)

private val MomentumDarkColors = darkColorScheme(
  primary = Color(0xFFF6FFF6),
  onPrimary = Color(0xFF003822),
  primaryContainer = Color(0xFF00FFAB),
  onPrimaryContainer = Color(0xFF00714A),
  inversePrimary = Color(0xFF006C46),
  secondary = Color(0xFFB5C8E2),
  onSecondary = Color(0xFF1F3246),
  secondaryContainer = Color(0xFF35485D),
  onSecondaryContainer = Color(0xFFA3B7D0),
  tertiary = Color(0xFFF4FFF8),
  onTertiary = Color(0xFF24342E),
  tertiaryContainer = Color(0xFFD2E4DB),
  onTertiaryContainer = Color(0xFF56665F),
  background = Color(0xFF0A1611),
  onBackground = Color(0xFFD7E6DD),
  surface = Color(0xFF0A1611),
  onSurface = Color(0xFFD7E6DD),
  surfaceVariant = Color(0xFF2B3831),
  onSurfaceVariant = Color(0xFFB9CBBE),
  surfaceTint = Color(0xFF00E297),
  inverseSurface = Color(0xFFD7E6DD),
  inverseOnSurface = Color(0xFF27332D),
  surfaceDim = Color(0xFF0A1611),
  surfaceBright = Color(0xFF2F3C36),
  surfaceContainerLowest = Color(0xFF05110C),
  surfaceContainerLow = Color(0xFF121E19),
  surfaceContainer = Color(0xFF16221D),
  surfaceContainerHigh = Color(0xFF202D27),
  surfaceContainerHighest = Color(0xFF2B3831),
  outline = Color(0xFF849589),
  outlineVariant = Color(0xFF3A4A40),
  error = Color(0xFFFFB4AB),
  onError = Color(0xFF690005),
  errorContainer = Color(0xFF93000A),
  onErrorContainer = Color(0xFFFFDAD6),
  scrim = Color(0xFF000000),
  primaryFixed = Color(0xFF4DFFB2),
  primaryFixedDim = Color(0xFF00E297),
  onPrimaryFixed = Color(0xFF002112),
  onPrimaryFixedVariant = Color(0xFF005234),
  secondaryFixed = Color(0xFFD0E4FE),
  secondaryFixedDim = Color(0xFFB5C8E2),
  onSecondaryFixed = Color(0xFF071D30),
  onSecondaryFixedVariant = Color(0xFF35485D),
  tertiaryFixed = Color(0xFFD5E7DE),
  tertiaryFixedDim = Color(0xFFB9CBC2),
  onTertiaryFixed = Color(0xFF0F1E19),
  onTertiaryFixedVariant = Color(0xFF3A4A44),
)

val AnybodyFontFamily = FontFamily(
  Font(R.font.anybody_400, FontWeight.Normal),
  Font(R.font.anybody_500, FontWeight.Medium),
  Font(R.font.anybody_600, FontWeight.SemiBold),
  Font(R.font.anybody_700, FontWeight.Bold),
  Font(R.font.anybody_800, FontWeight.ExtraBold),
  Font(R.font.anybody_900, FontWeight.Black),
)

val LexendFontFamily = FontFamily(
  Font(R.font.lexend_400, FontWeight.Normal),
  Font(R.font.lexend_500, FontWeight.Medium),
  Font(R.font.lexend_600, FontWeight.SemiBold),
  Font(R.font.lexend_700, FontWeight.Bold),
)

private val MomentumTypography = Typography(
  displayLarge = TextStyle(
    fontFamily = AnybodyFontFamily,
    fontWeight = FontWeight.ExtraBold,
    fontSize = 56.sp,
    lineHeight = 64.sp,
    letterSpacing = (-0.02).em,
  ),
  displayMedium = TextStyle(
    fontFamily = AnybodyFontFamily,
    fontWeight = FontWeight.ExtraBold,
    fontSize = 48.sp,
    lineHeight = 56.sp,
    letterSpacing = (-0.5).sp,
  ),
  displaySmall = TextStyle(
    fontFamily = AnybodyFontFamily,
    fontWeight = FontWeight.ExtraBold,
    fontSize = 40.sp,
    lineHeight = 48.sp,
    letterSpacing = (-0.02).em,
  ),
  headlineLarge = TextStyle(
    fontFamily = AnybodyFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 32.sp,
    lineHeight = 40.sp,
  ),
  headlineMedium = TextStyle(
    fontFamily = AnybodyFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
    lineHeight = 32.sp,
  ),
  headlineSmall = TextStyle(
    fontFamily = AnybodyFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 22.sp,
    lineHeight = 28.sp,
  ),
  titleLarge = TextStyle(
    fontFamily = LexendFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 20.sp,
    lineHeight = 28.sp,
  ),
  titleMedium = TextStyle(
    fontFamily = LexendFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = 24.sp,
  ),
  titleSmall = TextStyle(
    fontFamily = LexendFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 14.sp,
    lineHeight = 20.sp,
  ),
  bodyLarge = TextStyle(
    fontFamily = LexendFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
  ),
  bodyMedium = TextStyle(
    fontFamily = LexendFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp,
  ),
  bodySmall = TextStyle(
    fontFamily = LexendFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = 16.sp,
  ),
  labelLarge = TextStyle(
    fontFamily = LexendFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.1.em,
  ),
  labelMedium = TextStyle(
    fontFamily = LexendFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 11.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.4.sp,
  ),
  labelSmall = TextStyle(
    fontFamily = LexendFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 10.sp,
    lineHeight = 14.sp,
    letterSpacing = 0.4.sp,
  ),
)

private val MomentumShapes = Shapes(
  extraSmall = RoundedCornerShape(4.dp),
  small = RoundedCornerShape(8.dp),
  medium = RoundedCornerShape(12.dp),
  large = RoundedCornerShape(16.dp),
  extraLarge = RoundedCornerShape(24.dp),
)

@Composable
fun JumpTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = if (darkTheme) MomentumDarkColors else MomentumLightColors,
    typography = MomentumTypography,
    shapes = MomentumShapes,
    content = content,
  )
}
