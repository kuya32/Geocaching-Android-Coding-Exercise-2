package com.github.kuya32.geocachingandroidcodingexercise2.utils

sealed class Screen(val route: String) {
    object PermissionScreen: Screen("permission_screen")
    object MapViewScreen: Screen("map_view_screen")
}
