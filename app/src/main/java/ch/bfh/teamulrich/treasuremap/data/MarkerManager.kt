package ch.bfh.teamulrich.treasuremap.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.osmdroid.util.GeoPoint

class MarkerManager private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("TreasureMap", Context.MODE_PRIVATE)
    private val gson = Gson()


    fun getMarkers(): Map<String, GeoPoint> {
        val json = sharedPreferences.getString("markers", null) ?: return emptyMap()
        val type = object : TypeToken<Map<String, GeoPoint>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addMarker(marker: GeoPoint) {
        val markers = getMarkers().toMutableMap()
        markers[marker.toString()] = marker  // Use some unique key for each marker

        val editor = sharedPreferences.edit()
        val json = gson.toJson(markers)
        editor.putString("markers", json)
        editor.apply()
    }

    fun deleteMarker(marker: GeoPoint) {
        val markers = getMarkers().toMutableMap()
        markers.remove(marker.toString())  // Use the same unique key to remove the marker

        val editor = sharedPreferences.edit()
        val json = gson.toJson(markers)
        editor.putString("markers", json)
        editor.apply()
    }

    companion object {
        @Volatile
        private var INSTANCE: MarkerManager? = null

        fun getInstance(context: Context): MarkerManager = INSTANCE ?: synchronized(this) {
            INSTANCE ?: MarkerManager(context).also { INSTANCE = it }
        }
    }
}