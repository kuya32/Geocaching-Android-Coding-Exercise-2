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
import com.github.kuya32.geocachingandroidcodingexercise2.R
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.components.MapViewToolbar
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.util.UiEvent
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*
import kotlinx.coroutines.flow.collectLatest

/* Composable file consists of the map view toolbar and the Google map view that the user sees and
interacts with. Based on the user's interactions fires off UI events which call viewModel methods to
update the state of the map view.
 */
@ExperimentalPermissionsApi
@ExperimentalAnimationApi
@Composable
fun MapViewScreen(
    permissions: MultiplePermissionsState,
    context: Context = LocalContext.current,
    viewModel: MapViewModel = hiltViewModel()
) {
    viewModel.initializeDeviceLocation(permissions, context)

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
                                /* Event takes the user current location and sets the coordinates
                                to the pinned location to be marked on the map view.
                                */
                                is UiEvent.PinCurrentUserLocation -> {
                                    viewModel.setPinCoordinates(event.pinnedLocation)
                                }
                                /* Event takes the user current location coordinates to change the
                                map view camera focus to the user's current location.'
                                */
                                is UiEvent.ZoomToUserLocation -> {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newCameraPosition(
                                            CameraPosition.fromLatLngZoom(event.userLocation, 16.5f)
                                        )
                                    )
                                }
                                /* Event takes the saved pinned coordinates from the DataStore to place a
                                marker on the map view when loaded.
                                */
                                is UiEvent.PinSavedLocation -> {
                                    viewModel.setPinCoordinates(event.savedLocation)
                                }
                                else -> {}
                            }
                        }
                    }
                    viewModel.onEventMapView(MapViewEvent.PinLoaded)
                    if (viewModel.pinLat.value != 0.0 && viewModel.pinLng.value != 0.0) {
                        Marker(position = viewModel.getPinCoordinates())
                    }
                    /*
                    Caught this last minute. I believe that setting isMyLocationEnabled in the map
                    properties on line 63 automatically checks the device's location and displays
                    it on the map view. Commenting out line 63 and using the code below, draws a
                    circle on the map to show the user's current location. It doesn't look as great
                    as I wanted, but just wanted to show that the methods in the map view model
                    (initializeDeviceLocation and startLocationUpdates) do update the user's current
                    location and draws on the map view. I am keeping line 63 uncommented since it looks
                    more professional.
                     */
//                    Marker(
//                        position = viewModel.getUserCurrentCoordinates(),
//                        icon = viewModel.bitmapFromVector(context, R.drawable.ic_circle)
//                    )
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