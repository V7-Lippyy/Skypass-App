package com.example.skypass.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.skypass.ui.travel.ModernTravelLogScreen
import com.example.skypass.ui.travel.AddEditTravelEntryScreen
import com.example.skypass.ui.travel.TravelEntryDetailScreen

// Routes for travel feature
object TravelDestinations {
    private const val TRAVEL_ROUTE = "travel"
    const val TRAVEL_LIST_ROUTE = "$TRAVEL_ROUTE/list"
    const val TRAVEL_DETAIL_ROUTE = "$TRAVEL_ROUTE/detail/{entryId}"
    const val TRAVEL_ADD_ROUTE = "$TRAVEL_ROUTE/add"
    const val TRAVEL_EDIT_ROUTE = "$TRAVEL_ROUTE/edit/{entryId}"

    // Helper functions to create navigation routes with arguments
    fun travelDetailRoute(entryId: Long): String = "$TRAVEL_ROUTE/detail/$entryId"
    fun travelEditRoute(entryId: Long): String = "$TRAVEL_ROUTE/edit/$entryId"
}

@Composable
fun TravelNavGraph(
    navController: NavHostController,
    startDestination: String = TravelDestinations.TRAVEL_LIST_ROUTE,
    onNavigateBack: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Travel Log List Screen - now using Modern version
        composable(route = TravelDestinations.TRAVEL_LIST_ROUTE) {
            ModernTravelLogScreen(
                onNavigateBack = onNavigateBack,
                onEntryClick = { entryId ->
                    navController.navigate(TravelDestinations.travelDetailRoute(entryId))
                },
                onAddEntryClick = {
                    navController.navigate(TravelDestinations.TRAVEL_ADD_ROUTE)
                }
            )
        }

        // Travel Entry Detail Screen
        composable(
            route = TravelDestinations.TRAVEL_DETAIL_ROUTE,
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
                    navController.navigate(TravelDestinations.travelEditRoute(entryId))
                },
                onDeleteSuccess = {
                    navController.popBackStack()
                }
            )
        }

        // Add Travel Entry Screen
        composable(route = TravelDestinations.TRAVEL_ADD_ROUTE) {
            AddEditTravelEntryScreen(
                onNavigateBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        // Edit Travel Entry Screen
        composable(
            route = TravelDestinations.TRAVEL_EDIT_ROUTE,
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