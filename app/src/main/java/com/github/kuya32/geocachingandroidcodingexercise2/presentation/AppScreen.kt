package com.github.kuya32.geocachingandroidcodingexercise2.presentation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.map.MapViewScreen
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.permission.PermissionViewModel
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.permission.RationaleDialog
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.permission.SettingsDialog
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.util.UiEvent
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import kotlinx.coroutines.flow.collectLatest

// Used the AppScreen composable as a parent composable to hold permissions and map view screens.
@ExperimentalAnimationApi
@ExperimentalPermissionsApi
@Composable
fun AppScreen(
    navigateToSettingsScreen: () -> Unit,
    permissions: MultiplePermissionsState,
    viewModel: PermissionViewModel = hiltViewModel(),
) {

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                // Event that launches location permission requested when true
                is UiEvent.LaunchPermissions -> {
                    if (event.permissionLaunched) {
                        permissions.launchMultiplePermissionRequest()
                    }
                }
                // Event that navigates to the application settings when true
                is UiEvent.NavigateSettings -> {
                    if (event.settingsLaunched) {
                        navigateToSettingsScreen()
                    }
                }
                else -> {}
            }
        }
    }

    /* Granted permission sends the user to the map view while denied permission will prompt the
    user the need for location permission for the application to work. Used the Accompanist
    permissions API. Link: https://google.github.io/accompanist/permissions/ */
    when {
        permissions.allPermissionsGranted -> {
            MapViewScreen(permissions)
        }
        permissions.shouldShowRationale || !permissions.permissionRequested -> {
            if (viewModel.doNotShowRationale.value) {
                SettingsDialog(viewModel)
            } else {
                RationaleDialog(viewModel)
            }
        }
        !permissions.allPermissionsGranted -> {
            SettingsDialog(viewModel)
        }
    }
}
