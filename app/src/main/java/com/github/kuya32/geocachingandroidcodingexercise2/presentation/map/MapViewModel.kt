package com.github.kuya32.geocachingandroidcodingexercise2.presentation.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.webkit.PermissionRequest
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kuya32.geocachingandroidcodingexercise2.domain.models.Latitude
import com.github.kuya32.geocachingandroidcodingexercise2.domain.models.Longitude
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.permission.PermissionEvent
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.util.UiEvent
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalPermissionsApi
@HiltViewModel
class MapViewModel @Inject constructor(

): ViewModel() {

    private lateinit var locationCallback: LocationCallback

    private var _userCurrentLat = mutableStateOf(0.0)
    var userCurrentLat: MutableState<Double> = _userCurrentLat

    private var _userCurrentLng = mutableStateOf(0.0)
    var userCurrentLng: State<Double> = _userCurrentLng

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private fun setUserCurrentCoordinates(latLng: LatLng) {
        _userCurrentLat.value = latLng.latitude
        _userCurrentLng.value = latLng.longitude
    }

    fun onEventMapView(event: MapViewEvent) {
        when (event) {
            is MapViewEvent.PinCurrentLocation -> {

            }
            is MapViewEvent.NavigatePinnedLocation -> {

            }
            is MapViewEvent.NavigateUserLocation -> {
                viewModelScope.launch {
                    _eventFlow.emit(
                        UiEvent.NavigateToUserLocation(LatLng(_userCurrentLat.value, userCurrentLng.value))
                    )
                }
            }
            is MapViewEvent.CalculatedDistance -> {

            }
        }
    }

    fun getDeviceLocation(
        permissions: MultiplePermissionsState,
        context: Context
    ) {

        // Chunk of code is unnecessary since accompanist permission library already checks for location permissions. Included to satisfy error with retrieving fused location client.
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        try {
            startLocationUpdates(fusedLocationProviderClient, permissions, context)
            if (permissions.allPermissionsGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation

                locationResult.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val lastKnownLocation = task.result

                        if (lastKnownLocation != null) {
                            setUserCurrentCoordinates(
                                LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
                            )
                        }
                    } else {
                        Log.d("User Location", "Current user location null")
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.d("Exception", "Exception: ${e.message.toString()}")
        }
    }

    private fun startLocationUpdates(
        fusedLocationProviderClient: FusedLocationProviderClient,
        permissions: MultiplePermissionsState,
        context: Context
    ) {

        // Chunk of code is unnecessary since accompanist permission library already checks for location permissions. Included to satisfy error with requesting location updates.
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        if (permissions.allPermissionsGranted) {
            val locationRequest = LocationRequest.create().apply {
                interval = 1000
                fastestInterval = 500
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    result.locations.let { locations ->
                        for (location in locations) {
                            setUserCurrentCoordinates(LatLng(location.latitude, location.longitude))
                        }
                    }
                }
            }

            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }
}