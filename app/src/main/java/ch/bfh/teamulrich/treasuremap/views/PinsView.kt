package ch.bfh.teamulrich.treasuremap.views

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import ch.bfh.teamulrich.treasuremap.R
import ch.bfh.teamulrich.treasuremap.data.MarkerManager
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun PinsView() {
    val context = LocalContext.current
    val markerManager = MarkerManager.getInstance(context)
    val markers = remember { mutableStateOf(markerManager.getMarkers().values.toList()) }

    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBarWithPins()
        LazyColumn {
            itemsIndexed(markers.value) { index, marker ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .height(IntrinsicSize.Min),
                    shape = RoundedCornerShape(8.dp),
                    backgroundColor = Color.LightGray
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Pin ${index + 1}:",
                                style = TextStyle(
                                    fontSize = 25.sp, textDecoration = TextDecoration.Underline
                                ),
                            )
                            Spacer(modifier = Modifier.height(8.dp)) // Add a gap of 4.dp
                            Text(
                                text = "latitude: ${(marker.latitude * 1000000).toInt()}",
                                style = TextStyle(fontSize = 25.sp)
                            )
                            Spacer(modifier = Modifier.height(8.dp)) // Add a gap of 4.dp
                            Text(
                                text = "longitude: ${(marker.longitude * 1000000).toInt()}",
                                style = TextStyle(fontSize = 25.sp)
                            )
                        }
                        IconButton(onClick = {
                            markerManager.deleteMarker(marker)
                            markers.value =
                                markerManager.getMarkers().values.toList() // Update the state after deleting a marker
                        }) {
                            Icon(
                                painterResource(id = R.drawable.ic_delete),
                                contentDescription = "Delete pin"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopAppBarWithPins(context: Context = LocalContext.current) {
    TopAppBar(modifier = Modifier.zIndex(1f), title = { Text("Pins") }, actions = {
        Button(
            onClick = { sendPinsToLogApp(context) },
            modifier = Modifier
                .size(width = 130.dp, height = 50.dp)
                .clip(RoundedCornerShape(25.dp))
                .border(1.dp, Color.Black, RoundedCornerShape(25.dp))
                .align(Alignment.CenterVertically),

            ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painterResource(id = R.drawable.ic_send_to_server),
                    contentDescription = "Send to LogBook"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Send to LogBook", style = TextStyle(fontSize = 14.sp))
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
    })
}

fun sendPinsToLogApp(context: Context) {
    val markerManager = MarkerManager.getInstance(context)
    val markers = markerManager.getMarkers().values.toList()

    val points = JSONArray()
    for (marker in markers) {
        val point = JSONObject()
        point.put("lat", (marker.latitude * 1000000).toInt())
        point.put("lon", (marker.longitude * 1000000).toInt())
        points.put(point)
    }

    val log = JSONObject()
    log.put("task", "Schatzkarte")
    log.put("points", points)

    Log.i("LogBook", log.toString())

    val intent = Intent("ch.apprun.intent.LOG")
    intent.putExtra("ch.apprun.logmessage", log.toString())
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Log.e("LogBookError", " LogBook application is not installed on this device.")
        Toast.makeText(
            context,
            "LogBook application is not installed on this device.",
            Toast.LENGTH_LONG
        ).show()
    }
}
