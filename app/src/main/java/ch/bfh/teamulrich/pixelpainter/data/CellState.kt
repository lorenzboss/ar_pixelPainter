package ch.bfh.teamulrich.pixelpainter.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

class CellState {
    var color by mutableStateOf(Color.Transparent)
}