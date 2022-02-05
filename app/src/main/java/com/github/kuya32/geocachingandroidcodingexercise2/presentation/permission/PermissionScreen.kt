package com.github.kuya32.geocachingandroidcodingexercise2.presentation.permission

import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.github.kuya32.geocachingandroidcodingexercise2.R
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.map.MapViewModel
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.permission.PermissionEvent
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.ui.theme.*
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.util.UiEvent
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.flow.collectLatest

@ExperimentalPermissionsApi
@Composable
fun PermissionScreen(
    navController: NavController,
    navigateToSettingsScreen: () -> Unit,
    viewModel: PermissionViewModel = hiltViewModel(),
) {

    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.LaunchPermissions -> {
                    if (event.permissionLaunched) {
                        locationPermissionState.launchPermissionRequest()
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
        locationPermissionState.hasPermission -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Location permission granted!")
            }
        }
        locationPermissionState.shouldShowRationale || !locationPermissionState.permissionRequested -> {
            if (viewModel.doNotShowRationale.value) {
                SettingsDialog(viewModel)
            } else {
                RationaleDialog(
                    viewModel = viewModel
                )
            }
        }
        else -> {
            SettingsDialog(viewModel)
        }
    }
}

@ExperimentalPermissionsApi
@Composable
fun RationaleDialog(
    viewModel: PermissionViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.green))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceXXLarge)
                .clip(RoundedCornerShape(10))
                .background(color = colorResource(id = R.color.white)),
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            Image(
                painter = painterResource(id = R.drawable.ic_map),
                contentDescription = "Map Icon",
                modifier = Modifier
                    .weight(1.5f)
            )
            Spacer(modifier = Modifier.height(SpaceSmall))
            Text(
                text = "User location is needed for this app.",
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.green),
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp
                    )
            )
            Spacer(modifier = Modifier.height(SpaceSmall))
            Text(
                text =  "We'll just need your permission.",
                fontSize = 18.sp,
                color = colorResource(id = R.color.green),
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp
                    )
            )
            Spacer(modifier = Modifier.height(SpaceSmall))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .weight(2f)
                    .padding(bottom = SpaceThicc)
            ) {
                Button(
                    onClick = {
                        viewModel.onEventPermissions(PermissionEvent.OkRationale)
                    },
                    modifier = Modifier
                        .width(160.dp)
                ) {
                    Text(
                        text = "Ok",
                        fontSize = 18.sp,
                        color = colorResource(id = R.color.white),
                        modifier = Modifier
                            .padding(SpaceXSmall)
                    )
                }
                Spacer(modifier = Modifier.height(SpaceMedium))
                Text(
                    modifier = Modifier
                        .clickable {
                            viewModel.onEventPermissions(PermissionEvent.NopeRationale)
                        },
                    color = colorResource(id = R.color.green),
                    text = "Nope "
                )
            }
        }
    }
}

@Composable
fun SettingsDialog(
    viewModel: PermissionViewModel
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.green))
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .padding(SpaceXXLarge)
                .clip(RoundedCornerShape(10))
                .background(color = colorResource(id = R.color.white)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Location permission denied!",
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.green),
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(
                        top = 16.dp,
                        start = 16.dp,
                        end = 16.dp
                    )
            )
            Spacer(modifier = Modifier.height(SpaceSmall))
            Text(
                text =  "Please, grant us access in the application settings.",
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                color = colorResource(id = R.color.green),
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp
                    )
            )
            Spacer(modifier = Modifier.height(SpaceXXLarge))
            Button(
                onClick = {
                    viewModel.onEventPermissions(PermissionEvent.OpenSettings)
                },
                modifier = Modifier.padding(
                    bottom = 16.dp
                )
                ) {
                Text(
                    text = "Open Settings",
                    fontSize = 18.sp,
                    color = colorResource(id = R.color.white),
                    modifier = Modifier
                        .padding(SpaceXSmall)
                )
            }
        }
    }
}
