package ch.bfh.teamulrich.treasuremap

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ch.bfh.teamulrich.treasuremap.data.MarkerManager
import ch.bfh.teamulrich.treasuremap.ui.BottomBarNavigation
import ch.bfh.teamulrich.treasuremap.ui.theme.TreasureMapTheme
import ch.bfh.teamulrich.treasuremap.views.PinsView
import ch.bfh.teamulrich.treasuremap.views.TreasureMapView
import ch.bfh.teamulrich.treasuremap.views.WithPermission
import com.google.accompanist.permissions.ExperimentalPermissionsApi

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MarkerManager.getInstance(this)

        setContent {
            val navController = rememberNavController()

            TreasureMapTheme {
                WithPermission(permission = Manifest.permission.ACCESS_FINE_LOCATION,
                    noPermissionContent = {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Button(onClick = { it.launchPermissionRequest() }) {
                                Text("Grant permissions")
                            }
                        }
                    }) {
                    Scaffold(bottomBar = {
                        BottomBarNavigation(navController = navController)
                    }) { innerPadding ->
                        NavHost(
                            navController,
                            startDestination = Screen.Map.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(Screen.Map.route) { TreasureMapView() }
                            composable(Screen.Pin.route) { PinsView() }
                        }
                    }
                }
            }
        }
    }
}
