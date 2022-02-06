package com.github.kuya32.geocachingandroidcodingexercise2.presentation.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.compose.*
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.map.MapViewScreen
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.permission.PermissionScreen
import com.github.kuya32.geocachingandroidcodingexercise2.utils.Screen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState

@ExperimentalAnimationApi
@ExperimentalPermissionsApi
@Composable
fun Navigation(
    navController: NavHostController,
    scaffoldState: ScaffoldState,
    permission: PermissionState,
    context: Context = LocalContext.current
) {
    NavHost(
        navController = navController,
        startDestination = Screen.PermissionScreen.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(Screen.PermissionScreen.route) {
            PermissionScreen(
                navController = navController,
                navigateToSettingsScreen = {
                    context.startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", "com.github.kuya32.geocachingandroidcodingexercise2", null)
                        )
                    )
                },
                locationPermissionState = permission
            )
        }
        composable(Screen.MapViewScreen.route) {
            MapViewScreen()
        }
    }
}