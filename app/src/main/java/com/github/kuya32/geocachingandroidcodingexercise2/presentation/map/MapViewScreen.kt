package com.github.kuya32.geocachingandroidcodingexercise2.presentation.map

import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.components.MapViewToolbar
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.util.UiEvent
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
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
            onNavigateToUserClick = {
                viewModel.onEventMapView(MapViewEvent.ZoomUserLocation)
            }
        )

        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (viewModel.isUserLocationDetected.value) {

                val cameraPositionState = rememberCameraPositionState {
                    position =
                        CameraPosition.fromLatLngZoom(viewModel.getUserCurrentCoordinates(), 15f)
                }

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
                            myLocationButtonEnabled = false
                        )
                    )
                }

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = mapProperties,
                    uiSettings = uiSettings

                ) {
                    LaunchedEffect(key1 = true) {
                        viewModel.eventFlow.collectLatest { event ->
                            when (event) {
                                is UiEvent.PinCurrentUserLocation -> {
                                    viewModel.setPinCoordinates(event.pinnedLocation)
                                }
                                is UiEvent.ZoomToUserLocation -> {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newCameraPosition(
                                            CameraPosition.fromLatLngZoom(event.userLocation, 16.5f)
                                        )
                                    )
                                }
                                else -> {}
                            }
                        }
                    }
                    viewModel.checkAndSetPinCoordinates()
                    if (viewModel.pinLat.value != 0.0 && viewModel.pinLng.value != 0.0) {
                        Marker(position = viewModel.getPinCoordinates())
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