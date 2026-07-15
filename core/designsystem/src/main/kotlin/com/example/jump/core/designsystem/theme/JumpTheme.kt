package com.example.jump.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape

private val Ink = Color(0xFF07130E)
private val Pine = Color(0xFF102019)
private val DeepMint = Color(0xFF006B4A)
private val ElectricMint = Color(0xFF65E6A7)
private val MintMist = Color(0xFFB9F4D3)
private val Sky = Color(0xFFA8CEFF)
private val DeepSky = Color(0xFF235F91)
private val Amber = Color(0xFFFFB95F)
private val WarmWhite = Color(0xFFF7FAF6)
private val Paper = Color(0xFFFCFEFB)
private val SoftSurface = Color(0xFFEAF0EB)
private val StrongSurface = Color(0xFFDCE7DF)

private val DarkColors = darkColorScheme(
  primary = ElectricMint,
  onPrimary = Ink,
  primaryContainer = Color(0xFF004E35),
  onPrimaryContainer = Color(0xFFB7F4D1),
  secondary = Sky,
  onSecondary = Color(0xFF003351),
  secondaryContainer = Color(0xFF144B73),
  onSecondaryContainer = Color(0xFFD2E6FF),
  tertiary = Amber,
  onTertiary = Color(0xFF482A00),
  tertiaryContainer = Color(0xFF654000),
  onTertiaryContainer = Color(0xFFFFDDB4),
  background = Ink,
  onBackground = Color(0xFFE6F0E9),
  surface = Pine,
  onSurface = Color(0xFFE6F0E9),
  surfaceVariant = Color(0xFF384A40),
  onSurfaceVariant = Color(0xFFBBCBC0),
  surfaceContainerLowest = Color(0xFF05100B),
  surfaceContainerLow = Color(0xFF0D1A14),
  surfaceContainer = Color(0xFF12221A),
  surfaceContainerHigh = Color(0xFF1A2B22),
  surfaceContainerHighest = Color(0xFF22342A),
  outline = Color(0xFF85968B),
  outlineVariant = Color(0xFF3C4D43),
  error = Color(0xFFFFB4AB),
  onError = Color(0xFF690005),
  errorContainer = Color(0xFF93000A),
  onErrorContainer = Color(0xFFFFDAD6),
)

private val LightColors = lightColorScheme(
  primary = DeepMint,
  onPrimary = Color.White,
  primaryContainer = MintMist,
  onPrimaryContainer = Color(0xFF002115),
  secondary = DeepSky,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFD1E5FF),
  onSecondaryContainer = Color(0xFF001D33),
  tertiary = Color(0xFF865300),
  onTertiary = Color.White,
  tertiaryContainer = Color(0xFFFFDDB4),
  onTertiaryContainer = Color(0xFF2B1700),
  background = WarmWhite,
  onBackground = Ink,
  surface = Paper,
  onSurface = Ink,
  surfaceVariant = StrongSurface,
  onSurfaceVariant = Color(0xFF405048),
  surfaceContainerLowest = Color.White,
  surfaceContainerLow = Color(0xFFF0F5F0),
  surfaceContainer = SoftSurface,
  surfaceContainerHigh = StrongSurface,
  surfaceContainerHighest = Color(0xFFD3DED6),
  outline = Color(0xFF707A73),
  outlineVariant = Color(0xFFC0CAC2),
  error = Color(0xFFBA1A1A),
  onError = Color.White,
  errorContainer = Color(0xFFFFDAD6),
  onErrorContainer = Color(0xFF410002),
)

private val JumpTypography = Typography(
  displayLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Black, fontSize = 56.sp, lineHeight = 58.sp, letterSpacing = (-1.4).sp),
  displaySmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Black, fontSize = 40.sp, lineHeight = 43.sp, letterSpacing = (-0.8).sp),
  headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Black, fontSize = 34.sp, lineHeight = 38.sp, letterSpacing = (-0.5).sp),
  headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp, lineHeight = 33.sp, letterSpacing = (-0.25).sp),
  headlineSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 23.sp, lineHeight = 29.sp),
  titleLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp),
  titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp, letterSpacing = 0.1.sp),
  bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.15.sp),
  bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 21.sp, letterSpacing = 0.15.sp),
  labelLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.25.sp),
  labelMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 12.sp, lineHeight = 17.sp, letterSpacing = 0.9.sp),
)

private val JumpShapes = Shapes(
  extraSmall = RoundedCornerShape(8.dp),
  small = RoundedCornerShape(12.dp),
  medium = RoundedCornerShape(18.dp),
  large = RoundedCornerShape(24.dp),
  extraLarge = RoundedCornerShape(32.dp),
)

object JumpSpacing {
  val xxs = 4.dp
  val xs = 8.dp
  val sm = 12.dp
  val md = 16.dp
  val lg = 20.dp
  val xl = 24.dp
  val xxl = 32.dp
  val hero = 40.dp
}

@Composable
fun JumpTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
  MaterialTheme(
    colorScheme = if (darkTheme) DarkColors else LightColors,
    typography = JumpTypography,
    shapes = JumpShapes,
    content = content,
  )
}
