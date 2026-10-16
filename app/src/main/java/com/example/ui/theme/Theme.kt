package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val SkyBlueLightColorScheme = lightColorScheme(
    primary = SkyBlue600,
    onPrimary = PureWhite,
    primaryContainer = SkyBlue100,
    onPrimaryContainer = SkyBlue900,
    secondary = SkyBlue400,
    onSecondary = PureWhite,
    secondaryContainer = SkyBlue200,
    onSecondaryContainer = SkyBlue900,
    tertiary = SkyBlue700,
    onTertiary = PureWhite,
    background = SkyBlue50,
    onBackground = SlateText,
    surface = CardSurfaceWhite,
    onSurface = SlateText,
    surfaceVariant = SkyBlue100,
    onSurfaceVariant = SlateMuted,
    outline = SkyBlue200,
    outlineVariant = SkyBlue100
)

private val SkyBlueDarkColorScheme = darkColorScheme(
    primary = SkyBlue400,
    onPrimary = SkyBlue900,
    primaryContainer = SkyBlue800,
    onPrimaryContainer = SkyBlue100,
    secondary = SkyBlue300,
    onSecondary = SkyBlue900,
    background = Color(0xFF082F49),
    onBackground = PureWhite,
    surface = Color(0xFF0C4A6E),
    onSurface = PureWhite,
    surfaceVariant = Color(0xFF0369A1),
    onSurfaceVariant = SkyBlue100,
    outline = SkyBlue700
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> SkyBlueDarkColorScheme
        else -> SkyBlueLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
