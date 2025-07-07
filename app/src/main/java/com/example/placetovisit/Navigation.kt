package com.example.placetovisit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.placetovisit.data.Place

@Composable
fun Navigation(
    modifier: Modifier,
    navController: NavHostController = rememberNavController()
) {
    val placeViewModel: PlaceViewModel = viewModel()
    NavHost(navController = navController, startDestination = Screen.HomeScreen.route) {
        composable(Screen.HomeScreen.route) {
            HomeScreen(modifier = modifier, navController = navController)
        }
        composable(Screen.AddPage.route) {
            AddScreen(navController = navController, viewModel = placeViewModel)
        }
        composable(Screen.MapScreen.route) {
            MapPickerScreen(
                navController = navController,
                viewModel = placeViewModel
            )
        }
        composable(route = "editscreen/{placeId}") { backStackEntry ->
            val placeId = backStackEntry.arguments?.getString("placeId")?.toLongOrNull()

            if (placeId != null) {
                val placeFlow = placeViewModel.getPlaceById(placeId)
                val placeState = placeFlow.collectAsState(initial = null)
                val place = placeState.value

                if (place != null) {
                    EditScreen(
                        place = place,
                        navController = navController,
                        viewModel = placeViewModel
                    )
                }
            }
        }
    }
}
