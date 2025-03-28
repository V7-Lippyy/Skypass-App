// navigation/NavGraph.kt
package com.example.skypass.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.skypass.ui.compass.CompassScreen
import com.example.skypass.ui.home.HomeScreen
import com.example.skypass.ui.splash.SplashScreen
import com.example.skypass.ui.travel.AddEditTravelEntryScreen
import com.example.skypass.ui.travel.ModernTravelLogScreen
import com.example.skypass.ui.travel.TravelEntryDetailScreen
import com.example.skypass.ui.weather.WeatherScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Weather : Screen("weather")
    object Compass : Screen("compass")
    object TravelList : Screen("travel_list")
    object TravelDetail : Screen("travel_detail/{entryId}")
    object TravelAdd : Screen("travel_add")
    object TravelEdit : Screen("travel_edit/{entryId}")
}

// Helper functions for navigation
private fun createTravelDetailRoute(entryId: Long): String = "travel_detail/$entryId"
private fun createTravelEditRoute(entryId: Long): String = "travel_edit/$entryId"

@Composable
fun SkyPassNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onNavigateToHome = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToWeather = { navController.navigate(Screen.Weather.route) },
                onNavigateToCompass = { navController.navigate(Screen.Compass.route) },
                onNavigateToTravel = { navController.navigate(Screen.TravelList.route) }
            )
        }

        composable(Screen.Weather.route) {
            WeatherScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Compass.route) {
            CompassScreen(onNavigateBack = { navController.popBackStack() })
        }

        // Travel feature routes - using ModernTravelLogScreen now
        composable(Screen.TravelList.route) {
            ModernTravelLogScreen(
                onNavigateBack = { navController.popBackStack() },
                onEntryClick = { entryId ->
                    navController.navigate(createTravelDetailRoute(entryId))
                },
                onAddEntryClick = {
                    navController.navigate(Screen.TravelAdd.route)
                }
            )
        }

        composable(
            route = "travel_detail/{entryId}",
            arguments = listOf(
                navArgument("entryId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getLong("entryId") ?: 0L
            TravelEntryDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onEditClick = {
                    navController.navigate(createTravelEditRoute(entryId))
                },
                onDeleteSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.TravelAdd.route) {
            AddEditTravelEntryScreen(
                onNavigateBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        composable(
            route = "travel_edit/{entryId}",
            arguments = listOf(
                navArgument("entryId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getLong("entryId") ?: 0L
            AddEditTravelEntryScreen(
                onNavigateBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }
    }
}