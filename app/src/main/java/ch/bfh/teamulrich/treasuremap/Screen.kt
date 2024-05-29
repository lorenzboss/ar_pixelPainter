package ch.bfh.teamulrich.treasuremap

import androidx.annotation.DrawableRes

sealed class Screen(val route: String, @DrawableRes val icon: Int) {
    data object Map : Screen("Map", R.drawable.ic_map)
    data object Pin : Screen("Pins", R.drawable.ic_pin)
}