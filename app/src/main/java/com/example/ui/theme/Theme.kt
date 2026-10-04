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

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color(0xFF003544),
    primaryContainer = Color(0xFF004D63),
    onPrimaryContainer = Color(0xFFBBE9FF),
    secondary = WarmAmber,
    onSecondary = Color(0xFF452B00),
    secondaryContainer = Color(0xFF633F00),
    onSecondaryContainer = Color(0xFFFFDDB3),
    tertiary = MintTeal,
    onTertiary = Color(0xFF003832),
    background = DarkBg,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF006782),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBBE9FF),
    onPrimaryContainer = Color(0xFF001F29),
    secondary = Color(0xFF8B5000),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDDB3),
    onSecondaryContainer = Color(0xFF2C1600),
    tertiary = Color(0xFF006A5F),
    onTertiary = Color.White,
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun TownsquareTheme(
    themeConfig: com.example.ui.theme.ThemeConfig,
    content: @Composable () -> Unit
) {
    val colorScheme = if (themeConfig.isDark) {
        darkColorScheme(
            primary = themeConfig.primary,
            secondary = themeConfig.secondary,
            background = themeConfig.background,
            surface = themeConfig.surface
        )
    } else {
        lightColorScheme(
            primary = themeConfig.primary,
            secondary = themeConfig.secondary,
            background = themeConfig.background,
            surface = themeConfig.surface
        )
    }

    val typography = when (themeConfig.typographyStyle) {
        "monospace" -> CliDosTypography
        "broadsheet" -> FirebaseTypographyExtensions.BroadsheetTypography
        "inter" -> FirebaseTypographyExtensions.InterTypography
        "teletext" -> FirebaseTypographyExtensions.TeletextTypography
        "condensed" -> FirebaseTypographyExtensions.CondensedTypography
        else -> if (themeConfig.id == "cli_dos") CliDosTypography else Typography
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}

@Composable
fun OmniMediaTheme(
    themeConfig: com.example.ui.theme.ThemeConfig,
    content: @Composable () -> Unit
) {
    TownsquareTheme(
        themeConfig = themeConfig,
        content = content
    )
}
