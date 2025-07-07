package com.example.placetovisit

sealed class Screen(val route : String){
    object HomeScreen: Screen("home")
    object AddPage: Screen("AddPage")
    object MapScreen: Screen("mapscreen")
    object EditScreen: Screen("editscreen/{placeId}"){
        fun createRoute(placeId: Long) = "editscreen/$placeId"
    }
}