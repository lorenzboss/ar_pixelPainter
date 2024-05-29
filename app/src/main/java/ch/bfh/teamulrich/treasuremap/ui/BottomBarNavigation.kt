package ch.bfh.teamulrich.treasuremap.ui

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import ch.bfh.teamulrich.treasuremap.Screen

@Composable
fun BottomBarNavigation(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val screens = listOf(Screen.Map, Screen.Pin)

    BottomNavigation {
        screens.forEach {
            BottomNavigationItem(icon = {
                Icon(
                    painter = painterResource(it.icon), contentDescription = it.route
                )
            },
                label = { Text(it.route) },
                selected = currentRoute == it.route,
                selectedContentColor = MaterialTheme.colors.secondary,
                unselectedContentColor = MaterialTheme.colors.secondaryVariant,
                onClick = {
                    navController.navigate(it.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                })
        }
    }
}