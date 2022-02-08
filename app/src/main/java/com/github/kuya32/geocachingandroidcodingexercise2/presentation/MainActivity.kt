package com.github.kuya32.geocachingandroidcodingexercise2.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.github.kuya32.geocachingandroidcodingexercise2.Pin
import com.github.kuya32.geocachingandroidcodingexercise2.domain.PinSerializer
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.components.StandardScaffold
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.map.MapViewEvent
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.map.MapViewModel
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.ui.theme.*
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.util.Constants
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalAnimationApi
@ExperimentalPermissionsApi
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val mapViewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition() {
                mainViewModel.isLoading.value
            }
        }
        setContent {
            GeocachingAndroidCodingExercise2Theme {
                val locationPermissionsState = rememberMultiplePermissionsState(
                    permissions = listOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val scaffoldState = rememberScaffoldState()
                    StandardScaffold(
                        state = scaffoldState,
                        modifier = Modifier.fillMaxSize(),
                        showFabButton = locationPermissionsState.allPermissionsGranted,
                        onFabClick = {
                            mapViewModel.onEventMapView(MapViewEvent.PinUserLocation)
                        }
                    ) {
                        AppScreen(
                            navigateToSettingsScreen = {
                                startActivity(
                                    Intent(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", packageName, null)
                                    )
                                )
                            },
                            permissions = locationPermissionsState
                        )
                    }
                }
            }
        }
    }
}
