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
import androidx.compose.ui.unit.sp
import com.example.jump.core.designsystem.R

private val MomentumLightColors = lightColorScheme(
  primary = Color(0xFF005037),
  onPrimary = Color(0xFFFFFFFF),
  primaryContainer = Color(0xFF006B4A),
  onPrimaryContainer = Color(0xFF92E8BF),
  inversePrimary = Color(0xFF65E6A7),
  secondary = Color(0xFF276294),
  onSecondary = Color(0xFFFFFFFF),
  secondaryContainer = Color(0xFF91C5FD),
  onSecondaryContainer = Color(0xFF002F50),
  tertiary = Color(0xFF643E00),
  onTertiary = Color(0xFFFFFFFF),
  tertiaryContainer = Color(0xFF845300),
  onTertiaryContainer = Color(0xFFFFDDB4),
  background = Color(0xFFEEFDF4),
  onBackground = Color(0xFF121E19),
  surface = Color(0xFFEEFDF4),
  onSurface = Color(0xFF121E19),
  surfaceVariant = Color(0xFFDDECE3),
  onSurfaceVariant = Color(0xFF3F4943),
  surfaceTint = Color(0xFF005037),
  inverseSurface = Color(0xFF27312B),
  inverseOnSurface = Color(0xFFECF5EE),
  surfaceDim = Color(0xFFD7E6DD),
  surfaceBright = Color(0xFFF5FFF8),
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
  onErrorContainer = Color(0xFF410002),
  scrim = Color(0xFF000000),
  primaryFixed = Color(0xFF92E8BF),
  primaryFixedDim = Color(0xFF65E6A7),
  onPrimaryFixed = Color(0xFF002115),
  onPrimaryFixedVariant = Color(0xFF005037),
  secondaryFixed = Color(0xFFD1E5FF),
  secondaryFixedDim = Color(0xFF9ACBFF),
  onSecondaryFixed = Color(0xFF001D33),
  onSecondaryFixedVariant = Color(0xFF154A72),
  tertiaryFixed = Color(0xFFFFDDB4),
  tertiaryFixedDim = Color(0xFFFFB95F),
  onTertiaryFixed = Color(0xFF2B1700),
  onTertiaryFixedVariant = Color(0xFF643E00),
)

private val MomentumDarkColors = darkColorScheme(
  primary = Color(0xFF65E6A7),
  onPrimary = Color(0xFF003823),
  primaryContainer = Color(0xFF005037),
  onPrimaryContainer = Color(0xFF92E8BF),
  inversePrimary = Color(0xFF005037),
  secondary = Color(0xFF91C5FD),
  onSecondary = Color(0xFF003153),
  secondaryContainer = Color(0xFF174E77),
  onSecondaryContainer = Color(0xFFCBE5FF),
  tertiary = Color(0xFFFFB95F),
  onTertiary = Color(0xFF482A00),
  tertiaryContainer = Color(0xFF643E00),
  onTertiaryContainer = Color(0xFFFFDDB4),
  background = Color(0xFF030A07),
  onBackground = Color(0xFFDCEBE1),
  surface = Color(0xFF030A07),
  onSurface = Color(0xFFDCEBE1),
  surfaceVariant = Color(0xFF202C25),
  onSurfaceVariant = Color(0xFFBBC9BF),
  surfaceTint = Color(0xFF65E6A7),
  inverseSurface = Color(0xFFDCEBE1),
  inverseOnSurface = Color(0xFF27312B),
  surfaceDim = Color(0xFF030A07),
  surfaceBright = Color(0xFF28362E),
  surfaceContainerLowest = Color(0xFF010503),
  surfaceContainerLow = Color(0xFF09130E),
  surfaceContainer = Color(0xFF0F1A14),
  surfaceContainerHigh = Color(0xFF17231C),
  surfaceContainerHighest = Color(0xFF202C25),
  outline = Color(0xFF85968B),
  outlineVariant = Color(0xFF3D4A42),
  error = Color(0xFFFFB4AB),
  onError = Color(0xFF690005),
  errorContainer = Color(0xFF93000A),
  onErrorContainer = Color(0xFFFFDAD6),
  scrim = Color(0xFF000000),
  primaryFixed = Color(0xFF92E8BF),
  primaryFixedDim = Color(0xFF65E6A7),
  onPrimaryFixed = Color(0xFF002115),
  onPrimaryFixedVariant = Color(0xFF005037),
  secondaryFixed = Color(0xFFD1E5FF),
  secondaryFixedDim = Color(0xFF9ACBFF),
  onSecondaryFixed = Color(0xFF001D33),
  onSecondaryFixedVariant = Color(0xFF154A72),
  tertiaryFixed = Color(0xFFFFDDB4),
  tertiaryFixedDim = Color(0xFFFFB95F),
  onTertiaryFixed = Color(0xFF2B1700),
  onTertiaryFixedVariant = Color(0xFF643E00),
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
    letterSpacing = (-0.6).sp,
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
    letterSpacing = (-0.4).sp,
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
    letterSpacing = 0.2.sp,
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
