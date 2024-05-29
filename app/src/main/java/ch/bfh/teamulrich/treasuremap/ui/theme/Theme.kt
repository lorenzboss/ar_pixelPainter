package ch.bfh.teamulrich.treasuremap.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = bfhOrange,
    primaryVariant = bfhOrange,
    secondary = bfhOrange,
    secondaryVariant = bfhOrange,
    background = colBlack,
    surface = bfhGrey,
    error = bfhDarkRed,
    onPrimary = colBlack,
    onSecondary = colBlack,
    onBackground = colWhite,
    onSurface = colWhite,
    onError = colBlack
)

private val LightColorPalette = lightColors(
    primary = bfhOrange,
    primaryVariant = bfhOrange,
    secondary = colBlack,
    secondaryVariant = colBlack,
    background = colWhite,
    surface = colWhite,
    error = bfhMediumRed,
    onPrimary = colBlack,
    onSecondary = colBlack,
    onBackground = colBlack,
    onSurface = colBlack,
    onError = colWhite
)

@Composable
fun TreasureMapTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (isSystemInDarkTheme()) DarkColorPalette else LightColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}