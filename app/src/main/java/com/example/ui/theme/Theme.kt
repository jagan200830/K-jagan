package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
  primary = BrandPrimary,
  onPrimary = Color.White,
  primaryContainer = Blue100,
  onPrimaryContainer = BrandPrimary,
  secondary = BrandSecondary,
  onSecondary = Color.White,
  secondaryContainer = Teal100,
  onSecondaryContainer = BrandSecondary,
  tertiary = BrandAccent,
  background = Slate50,
  onBackground = Slate900,
  surface = Color.White,
  onSurface = Slate900,
  surfaceVariant = Slate100,
  onSurfaceVariant = Slate700,
  error = BrandEmergency,
  onError = Color.White,
  outline = Slate200,
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = LightColorScheme,
    typography = Typography,
    content = content
  )
}

