package com.github.kuya32.geocachingandroidcodingexercise2.presentation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.github.kuya32.geocachingandroidcodingexercise2.R
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.map.MapViewScreen
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.permission.PermissionViewModel
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.permission.RationaleDialog
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.permission.SettingsDialog
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.ui.theme.*
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.util.UiEvent
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import kotlinx.coroutines.flow.collectLatest

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
                is UiEvent.LaunchPermissions -> {
                    if (event.permissionLaunched) {
                        permissions.launchMultiplePermissionRequest()
                    }
                }
                is UiEvent.NavigateSettings -> {
                    if (event.settingsLaunched) {
                        navigateToSettingsScreen()
                    }
                }
            }
        }
    }

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
