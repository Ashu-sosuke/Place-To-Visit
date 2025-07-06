package com.example.placetovisit

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.LatLng

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
    }
}
