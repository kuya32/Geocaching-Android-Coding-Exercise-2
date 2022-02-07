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
        var isMapLoaded by remember { mutableStateOf(false) }

        Box(
            Modifier.fillMaxSize()
        ) {
            viewModel.getDeviceLocation(permissions, context)

            // Observing and controlling the camera's state can be done with a CameraPositionState
            val cameraPositionState = rememberCameraPositionState()

            val mapProperties by remember {
                mutableStateOf(
                    MapProperties(
                        mapType = MapType.NORMAL,
                        isMyLocationEnabled = true
                    )
                )
            }
            val uiSettings by remember {
                mutableStateOf(
                    MapUiSettings(
                        compassEnabled = true,
                        myLocationButtonEnabled = false
                    )
                )
            }
            val ticker by remember { mutableStateOf(0) }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                properties = mapProperties,
                uiSettings = uiSettings,
                onMapLoaded = {
                    isMapLoaded = true
                }
            ) {
                LaunchedEffect(key1 = true) {
                    viewModel.eventFlow.collectLatest { event ->
                        when (event) {
                            is UiEvent.NavigateToUserLocation -> {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newCameraPosition(
                                        CameraPosition.fromLatLngZoom(event.userLocation, 16.5f)
                                    )
                                )
                            }
                        }
                    }
                }
                val userLocation =
                    LatLng(viewModel.userCurrentLat.value, viewModel.userCurrentLng.value)
                if (isMapLoaded) {
                    cameraPositionState.position =
                        CameraPosition.fromLatLngZoom(userLocation, 16.5f)
                }
            }
        }
    }
}