package com.github.kuya32.geocachingandroidcodingexercise2.presentation.map

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@ExperimentalAnimationApi
@Composable
fun MapViewScreen(
    context: Context = LocalContext.current,
    viewModel: MapViewModel = hiltViewModel()
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(text = "Geocaching")
            },
            modifier = Modifier
                .fillMaxWidth(),
            actions = {
                IconButton(
                    onClick = { /*TODO*/ }
                ) {
//                    Icon(
//                        imageVector = ,
//                        contentDescription =
//                    )
                }
                IconButton(
                    onClick = { /*TODO*/ }
                ) {
//                    Icon(
//                        imageVector = ,
//                        contentDescription =
//                    )
                }
                IconButton(
                    onClick = { /*TODO*/ }
                ) {
//                    Icon(
//                        imageVector = ,
//                        contentDescription =
//                    )
                }
            },
            elevation = 8.dp,
            backgroundColor = MaterialTheme.colors.background,
            contentColor = MaterialTheme.colors.onBackground
        )
        var isMapLoaded by remember { mutableStateOf(false) }

        Box(Modifier.fillMaxSize()) {
            GoogleMapView(
                modifier = Modifier.matchParentSize(),
                onMapLoaded = {
                    isMapLoaded = true
                }
            )
        }
    }
}

@Composable
private fun GoogleMapView(modifier: Modifier, onMapLoaded: () -> Unit) {
    val singapore = LatLng(1.35, 103.87)
    // Observing and controlling the camera's state can be done with a CameraPositionState
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 11f)
    }

    var mapProperties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }
    var uiSettings by remember { mutableStateOf(MapUiSettings(compassEnabled = false)) }
    var ticker by remember { mutableStateOf(0) }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = mapProperties,
        uiSettings = uiSettings,
        onMapLoaded = onMapLoaded,
        googleMapOptionsFactory = {
            GoogleMapOptions().camera(CameraPosition.fromLatLngZoom(singapore, 11f))
        },
        onPOIClick = {
            Log.d(TAG, "POI clicked: ${it.name}")
        }
    ) {
        // Drawing on the map is accomplished with a child-based API
        Marker(
            position = singapore,
            title = "Zoom in has been tapped $ticker times.",
            onClick = {
                println("${it.title} was clicked")
                false
            }
        )
        Circle(
            center = singapore,
            fillColor = MaterialTheme.colors.secondary,
            strokeColor = MaterialTheme.colors.secondaryVariant,
            radius = 1000.0,
        )
    }
}