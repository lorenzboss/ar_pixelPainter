package ch.bfh.teamulrich.pixelpainter

import androidx.annotation.DrawableRes

sealed class Screen(val route: String, @DrawableRes val icon: Int) {
    data object Canvas : Screen("Canvas", R.drawable.ic_canvas)
}