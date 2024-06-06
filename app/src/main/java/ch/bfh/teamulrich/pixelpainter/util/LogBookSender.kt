package ch.bfh.teamulrich.pixelpainter.util


import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import ch.bfh.teamulrich.pixelpainter.data.DrawingBoardState

object LogBookSender {
    fun sendPinsToLogApp(context: Context, drawingBoardState: DrawingBoardState) {
        val log = drawingBoardState.toJson()
        Log.i("LogBook", log.toString())

        val intent = Intent("ch.apprun.intent.LOG")
        intent.putExtra("ch.apprun.logmessage", log.toString())
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.e("LogBookError", " LogBook application is not installed on this device.")
            Toast.makeText(
                context, "LogBook application is not installed on this device.", Toast.LENGTH_LONG
            ).show()
        }
    }
}