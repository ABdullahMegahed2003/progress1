package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = GymGreenPrimary,
    onPrimary = GymDarkBackground,
    primaryContainer = GymGreenContainer,
    onPrimaryContainer = GymGreenAccent,
    secondary = GymGreenAccent,
    onSecondary = GymDarkBackground,
    secondaryContainer = GymDarkSurfaceVariant,
    onSecondaryContainer = GymTextWhite,
    tertiary = GymGold,
    onTertiary = GymDarkBackground,
    background = GymDarkBackground,
    onBackground = GymTextWhite,
    surface = GymDarkSurface,
    onSurface = GymTextWhite,
    surfaceVariant = GymDarkSurfaceVariant,
    onSurfaceVariant = GymTextMuted,
    outline = GymDarkOutline,
    error = GymRed,
    onError = GymTextWhite
)

@Composable
fun TaqadomTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme // Strictly enforce dark athletic gym aesthetic as requested

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = GymDarkBackground.toArgb()
                it.navigationBarColor = GymDarkBackground.toArgb()
                val controller = WindowCompat.getInsetsController(it, view)
                controller.isAppearanceLightStatusBars = false
                controller.isAppearanceLightNavigationBars = false
            }
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
