package ch.bfh.teamulrich.pixelpainter.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

class DrawingBoardState {
    val cells = List(169) { CellState() }
    var selectedColor by mutableStateOf(Color.Transparent)

    fun toJson(): JSONObject {
        val log = JSONObject()
        log.put("task", "Pixelmaler")
        val pixels = JSONArray()
        cells.forEachIndexed { index, cellState ->
            if (cellState.color != Color.Transparent) {
                val pixel = JSONObject()
                pixel.put("y", (index / 13).toString())
                pixel.put("x", (index % 13).toString())
                val colorInt = cellState.color.toArgb()
                val colorStr = "#${
                    colorInt.toUInt().toString(16).padStart(8, '0')
                        .uppercase(Locale.getDefault())
                }"
                pixel.put("color", colorStr)
                pixels.put(pixel)
            }
        }
        log.put("pixels", pixels)
        return log
    }
}