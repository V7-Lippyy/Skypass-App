// navigation/NavGraph.kt
package com.example.skypass.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.skypass.ui.compass.CompassScreen
import com.example.skypass.ui.home.HomeScreen
import com.example.skypass.ui.splash.SplashScreen
import com.example.skypass.ui.weather.WeatherScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Weather : Screen("weather")
    object Compass : Screen("compass")
}

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
                onNavigateToCompass = { navController.navigate(Screen.Compass.route) }
            )
        }

        composable(Screen.Weather.route) {
            WeatherScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Compass.route) {
            CompassScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}