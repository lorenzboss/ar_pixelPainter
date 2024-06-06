package ch.bfh.teamulrich.pixelpainter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ch.bfh.teamulrich.pixelpainter.ui.BottomBarNavigation
import ch.bfh.teamulrich.pixelpainter.ui.theme.PixelPainterTheme
import ch.bfh.teamulrich.pixelpainter.views.CanvasView
import com.google.accompanist.permissions.ExperimentalPermissionsApi

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            PixelPainterTheme {
                Scaffold(bottomBar = {
                    BottomBarNavigation(navController = navController)
                }) { innerPadding ->
                    NavHost(
                        navController,
                        startDestination = Screen.Canvas.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Canvas.route) { CanvasView() }
                    }
                }
            }
        }
    }
}

