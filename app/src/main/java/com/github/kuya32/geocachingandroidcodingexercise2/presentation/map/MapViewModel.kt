package com.github.kuya32.geocachingandroidcodingexercise2.presentation.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kuya32.geocachingandroidcodingexercise2.Pin
import com.github.kuya32.geocachingandroidcodingexercise2.domain.PinProtoRepositoryImpl
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.util.UiEvent
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalPermissionsApi
@HiltViewModel
class MapViewModel @Inject constructor(
    private val pinProtoRepositoryImpl: PinProtoRepositoryImpl
): ViewModel() {

    private lateinit var locationCallback: LocationCallback

    var isUserLocationDetected = mutableStateOf(false)

    private var _userCurrentLat = mutableStateOf(0.0)
    var userCurrentLat: MutableState<Double> = _userCurrentLat

    private var _userCurrentLng = mutableStateOf(0.0)
    var userCurrentLng: MutableState<Double> = _userCurrentLng

    private var _pinLat = mutableStateOf(0.0)
    var pinLat: MutableState<Double> = _pinLat

    private var _pinLng = mutableStateOf(0.0)
    var pinLng: MutableState<Double> = _pinLng

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private fun setUserCurrentCoordinates(latLng: LatLng) {
        _userCurrentLat.value = latLng.latitude
        _userCurrentLng.value = latLng.longitude
    }

    fun getUserCurrentCoordinates(): LatLng {
        return LatLng(_userCurrentLat.value, _userCurrentLng.value)
    }

    fun setPinCoordinates(latLng: LatLng) {
        _pinLat.value = latLng.latitude
        _pinLng.value = latLng.longitude
    }

    fun getPinCoordinates(): LatLng {
        return LatLng(_pinLat.value, _pinLng.value)
    }

    fun checkAndSetPinCoordinates(): LatLng {
        viewModelScope.launch(Dispatchers.IO) {
            val lat = pinProtoRepositoryImpl.getPinnedLocation().latitude
            val lng = pinProtoRepositoryImpl.getPinnedLocation().longitude
            println("$lat : $lng")
            if (lat != 0.0 && lng != 0.0) {
                _pinLat.value = lat
                _pinLng.value = lng
            }
        }
        return LatLng(_pinLat.value, _pinLng.value)
    }

    fun onEventMapView(event: MapViewEvent) {
        when (event) {
            is MapViewEvent.PinUserLocation -> {
                viewModelScope.launch {
                    // Saves pinned location to Proto Datastore so that we can mark the map with pinned location even after the app is closed
                    pinProtoRepositoryImpl.updatePinnedLocation(LatLng(userCurrentLat.value, userCurrentLng.value))
                    _eventFlow.emit(
                        UiEvent.PinCurrentUserLocation(LatLng(userCurrentLat.value, userCurrentLng.value))
                    )
                }
            }
            is MapViewEvent.ZoomPinnedLocation -> {
                viewModelScope.launch {
                    _eventFlow.emit(
                        UiEvent.ZoomToUserLocation(getPinCoordinates())
                    )
                }
            }
            is MapViewEvent.ZoomUserLocation -> {
                viewModelScope.launch {
                    _eventFlow.emit(
                        UiEvent.ZoomToUserLocation(LatLng(userCurrentLat.value, userCurrentLng.value))
                    )
                }
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
                            isUserLocationDetected.value = true
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