package com.example.placetovisit

sealed class Screen(val route : String){
    object HomeScreen: Screen("home")
    object AddPage: Screen("AddPahe")
}