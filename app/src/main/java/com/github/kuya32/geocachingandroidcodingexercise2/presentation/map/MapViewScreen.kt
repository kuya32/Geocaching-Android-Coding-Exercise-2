package com.github.kuya32.geocachingandroidcodingexercise2.presentation.map

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.kuya32.geocachingandroidcodingexercise2.R
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.components.MapViewToolbar
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.util.UiEvent
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import kotlinx.coroutines.flow.collectLatest

@ExperimentalPermissionsApi
@ExperimentalAnimationApi
@Composable
fun MapViewScreen(
    permissions: MultiplePermissionsState,
    context: Context = LocalContext.current,
    viewModel: MapViewModel = hiltViewModel()
) {
    viewModel.getDeviceLocation(permissions, context)

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        MapViewToolbar(
            modifier = Modifier.fillMaxWidth(),
            onNavigateToPinClick = {
                viewModel.onEventMapView(MapViewEvent.NavigatePinnedLocation)
            },
            onNavigateToUserClick = {
                viewModel.onEventMapView(MapViewEvent.NavigateUserLocation)
            },
            onCalculateDistanceClick = {
                viewModel.onEventMapView(MapViewEvent.CalculatedDistance)
            }
        )

        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (viewModel.isUserLocationDetected.value) {
                val userLocation = viewModel.getUserCurrentCoordinates()
                println("${userLocation.latitude}HELLO")
                // Observing and controlling the camera's state can be done with a CameraPositionState
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(userLocation, 15f)
                }

                val mapProperties by remember {
                    mutableStateOf(MapProperties(
                        mapType = MapType.NORMAL,
                        isMyLocationEnabled = true
                    ))
                }
                val uiSettings by remember { mutableStateOf(MapUiSettings(
                    compassEnabled = true,
                    myLocationButtonEnabled = false
                )) }

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = mapProperties,
                    uiSettings = uiSettings
                ) {
                    LaunchedEffect(key1 = true) {
                        viewModel.eventFlow.collectLatest { event ->
                            when (event) {
                                is UiEvent.NavigateToUserLocation -> {
                                    cameraPositionState.animate(CameraUpdateFactory.newCameraPosition(
                                        CameraPosition.fromLatLngZoom(event.userLocation, 16.5f)))
                                }
                            }
                        }
                    }
                }
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.size(80.dp),
                    color = MaterialTheme.colors.onBackground
                )
            }
        }
    }
}