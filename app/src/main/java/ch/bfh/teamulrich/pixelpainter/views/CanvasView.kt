package ch.bfh.teamulrich.pixelpainter.views

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import ch.bfh.teamulrich.pixelpainter.R
import ch.bfh.teamulrich.pixelpainter.data.CellState
import ch.bfh.teamulrich.pixelpainter.data.DrawingBoardState
import ch.bfh.teamulrich.pixelpainter.util.LogBookSender.sendPinsToLogApp

@Composable
fun CanvasView() {
    val drawingBoardState = remember { DrawingBoardState() }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(modifier = Modifier.zIndex(1f), title = { Text("Pixel Painter") })
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            DrawingBoard(drawingBoardState)
            Spacer(modifier = Modifier.height(40.dp))
            ColorPalette(drawingBoardState)
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ClearButton(drawingBoardState)
                Spacer(modifier = Modifier.height(15.dp))
                SendButton(context = context, drawingBoardState = drawingBoardState)
            }
        }
    }
}

@Composable
fun DrawingBoard(drawingBoardState: DrawingBoardState) {
    val rows = drawingBoardState.cells.chunked(13)
    LazyColumn(modifier = Modifier.border(1.dp, Color.Black, RectangleShape)) {
        items(rows.size) { rowIndex ->
            Row {
                rows[rowIndex].forEachIndexed { cellIndex, cellState ->
                    CellView(cellState = cellState,
                        cellIndex = rowIndex * 13 + cellIndex,
                        onClicked = { index ->
                            drawingBoardState.cells[index].color = drawingBoardState.selectedColor
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun CellView(cellState: CellState, cellIndex: Int, onClicked: (pixelIndex: Int) -> Unit) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val padding = 16.dp * 2
    val canvasWidth = screenWidth - padding
    val cellSize = canvasWidth / 13

    Box(modifier = Modifier
        .background(color = cellState.color, shape = RectangleShape)
        .width(cellSize)
        .height(cellSize)
        .drawWithContent {
            drawContent()
            drawLine(
                color = Color.Black,
                start = Offset(size.width, 0f),
                end = Offset(size.width, size.height),
                strokeWidth = 1.dp.toPx(),
                cap = StrokeCap.Square
            )
            drawLine(
                color = Color.Black,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = 1.dp.toPx(),
                cap = StrokeCap.Square
            )
        }
        .clickable {
            onClicked(cellIndex)
            Log.i(
                "PixelPainter",
                "Pixel at index $cellIndex painted with color ${cellState.color}"
            )
        }) {}
}

@Composable
fun ColorPalette(drawingBoardState: DrawingBoardState) {
    val colors = listOf(
        Color(0xFF1364B7),
        Color(0xFF13B717),
        Color(0xFFFFEA00),
        Color(0xFFD90505),
        Color(0xFF000000),
        Color(0xFFB7B7B7),
        Color.Transparent
    )
    Row {
        colors.forEachIndexed { index, color ->
            IconButton(
                onClick = { drawingBoardState.selectedColor = color },
                modifier = Modifier
                    .background(color = color)
                    .border(1.dp, Color.Black)
                    .padding(4.dp)
                    .weight(1f)
                    .aspectRatio(1f)
            ) {
                if (color == drawingBoardState.selectedColor) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Select color",
                        tint = if (color == Color.Black) Color.White else Color.Black
                    )
                }
            }
            if (index < colors.size - 1) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

@Composable
fun ClearButton(drawingBoardState: DrawingBoardState) {
    Button(onClick = {
        drawingBoardState.cells.forEach { cellState ->
            cellState.color = Color.Transparent
        }
    }) {
        Icon(
            painter = painterResource(id = R.drawable.ic_delete), contentDescription = "Delete Icon"
        )
        Text("  Clear Canvas")
    }
}

@Composable
fun SendButton(context: Context, drawingBoardState: DrawingBoardState) {
    Button(onClick = { sendPinsToLogApp(context, drawingBoardState) }) {
        Icon(
            painter = painterResource(id = R.drawable.ic_send),
            contentDescription = "Send to LogBook"
        )
        Text("  Send to LogBook")
    }
}

