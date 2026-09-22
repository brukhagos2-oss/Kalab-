package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme =
    darkColorScheme(
        primary = GoldPrimary,
        onPrimary = BgDark,
        primaryContainer = GoldDark,
        onPrimaryContainer = GoldAccent,
        secondary = CyanEntry,
        onSecondary = BgDark,
        secondaryContainer = CyanDark,
        onSecondaryContainer = CyanLight,
        tertiary = EmeraldProfit,
        onTertiary = BgDark,
        tertiaryContainer = EmeraldDark,
        onTertiaryContainer = EmeraldLight,
        error = RoseRisk,
        onError = TextPrimary,
        errorContainer = RoseDark,
        onErrorContainer = RoseLight,
        background = BgDark,
        onBackground = TextPrimary,
        surface = CardDark,
        onSurface = TextPrimary,
        surfaceVariant = CardDarkElevated,
        onSurfaceVariant = TextSecondary,
        outline = BorderDark,
        outlineVariant = BorderGold
    )

@Composable
fun AurumBrainTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

