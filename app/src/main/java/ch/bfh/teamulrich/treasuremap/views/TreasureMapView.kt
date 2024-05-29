package ch.bfh.teamulrich.treasuremap.views

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import ch.bfh.teamulrich.treasuremap.R
import ch.bfh.teamulrich.treasuremap.data.MarkerManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


@Composable
fun TreasureMapView() {
    val context = LocalContext.current
    val markerManager = MarkerManager.getInstance(context)
    val mapView = rememberMapViewWithLifecycle()

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
        .setIntervalMillis(2000)
        .setMinUpdateIntervalMillis(100)
        .setWaitForAccurateLocation(false)
        .setMaxUpdateDelayMillis(2000)
        .build()


    val currentLocationMarker = Marker(mapView).apply {
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        icon = AppCompatResources.getDrawable(context, R.drawable.ic_current_location)
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                val userGeoPoint = GeoPoint(location.latitude, location.longitude)
                currentLocationMarker.position = userGeoPoint
                mapView.overlays.remove(currentLocationMarker)
                mapView.overlays.add(currentLocationMarker)
                mapView.invalidate()
            }
        }
    }

    if (ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    // Add this line to create a state for the markers
    val markers = remember { mutableStateOf(markerManager.getMarkers().values.toList()) }

    LaunchedEffect(Unit) {
        centerMapOnLocation(fusedLocationClient, mapView)
    }

    Column {
        TopAppBarWithMapView(markerManager, mapView, markers)

        AndroidView({ mapView }) { mapView ->

            Configuration.getInstance()
                .load(
                    context,
                    context.getSharedPreferences(
                        context.packageName + "_preferences",
                        Context.MODE_PRIVATE
                    )
                )

            mapView.setTileSource(TileSourceFactory.MAPNIK)
            mapView.setMultiTouchControls(true)
            mapView.controller.setZoom(20.0)
            mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
            mapView.setMultiTouchControls(true)

            mapView.overlays.clear()

            // Use the markers state to display the markers on the map
            markers.value.forEach { marker ->
                val startMarker = Marker(mapView)
                startMarker.position = GeoPoint(marker.latitude, marker.longitude)
                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                startMarker.icon = AppCompatResources.getDrawable(context, R.drawable.ic_red_pin)
                startMarker.setOnMarkerClickListener { _, _ ->
                    markerManager.deleteMarker(marker)

                    // Update the markers state after a marker is deleted
                    markers.value = markerManager.getMarkers().values.toList()
                    true
                }
                mapView.overlays.add(startMarker)
            }

            mapView.invalidate()
        }
    }
}

@Composable
fun TopAppBarWithMapView(
    markerManager: MarkerManager,
    mapView: MapView,
    markers: MutableState<List<GeoPoint>>
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(LocalContext.current)

    TopAppBar(modifier = Modifier.zIndex(1f),
        title = { Text("Map") }, actions = {
            IconButton(onClick = { addMarker(fusedLocationClient, markerManager, markers) }) {
                Icon(
                    painterResource(id = R.drawable.ic_add_pin), contentDescription = "Add pin"
                )
            }

            IconButton(onClick = { centerMapOnLocation(fusedLocationClient, mapView) }) {
                Icon(
                    painterResource(id = R.drawable.ic_current_location),
                    contentDescription = "Center on my location"
                )
            }
        })
}

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    DisposableEffect(mapView) {
        mapView.onResume()
        onDispose {
            mapView.onPause()
        }
    }

    return mapView
}

private fun addMarker(
    fusedLocationClient: FusedLocationProviderClient,
    markerManager: MarkerManager,
    markers: MutableState<List<GeoPoint>>
) {
    try {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val userGeoPoint = GeoPoint(
                    location.latitude, location.longitude
                )
                markerManager.addMarker(userGeoPoint)

                // Update the markers state after a new marker is added
                markers.value = markerManager.getMarkers().values.toList()
            } else {
                Log.e("MainActivity", "No last known location")
            }
        }
    } catch (e: SecurityException) {
        Log.e("MainActivity", "Location permission not granted")
    }
}

private fun centerMapOnLocation(
    fusedLocationClient: FusedLocationProviderClient,
    mapView: MapView
) {
    try {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val userGeoPoint = GeoPoint(
                    location.latitude, location.longitude
                )
                mapView.controller.setCenter(userGeoPoint)
            } else {
                Log.e("MainActivity", "No last known location")
            }
        }
    } catch (e: SecurityException) {
        Log.e("MainActivity", "Location permission not granted")
    }
}


