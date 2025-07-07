package com.example.placetovisit.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val AdventureGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF00C9FF),
        Color(0xFF92FE9D),
        Color(0xFFFFDEE9),
        Color(0xFFFF9A9E)
    )
)

val DarkEleganceGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF0F2027), // dark cyan/green
        Color(0xFF203A43), // deep blue-gray
        Color(0xFF2C5364)  // navy blue
    )
)
val SunsetGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFFFF6E7F), // Red Pink
        Color(0xFFFFB88C), // Peach
        Color(0xFFFFE29F)  // Yellow Orange
    )
)
