package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = CosmicGold,
    onPrimary = CosmicDark,
    primaryContainer = CosmicGoldGlow.copy(alpha = 0.2f),
    onPrimaryContainer = CosmicGold,
    secondary = CosmicCyan,
    onSecondary = CosmicDark,
    tertiary = CosmicPurple,
    onTertiary = CosmicDark,
    background = CosmicDark,
    onBackground = CosmicTextPrimary,
    surface = CosmicDarkSurface,
    onSurface = CosmicTextPrimary,
    surfaceVariant = CosmicSurfaceVariant,
    onSurfaceVariant = CosmicTextSecondary,
    outline = CosmicBorder
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Planetarium default dark space aesthetic
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

