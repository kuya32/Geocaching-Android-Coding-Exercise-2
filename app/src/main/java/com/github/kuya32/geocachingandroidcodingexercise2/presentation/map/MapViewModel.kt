package com.github.kuya32.geocachingandroidcodingexercise2.presentation.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kuya32.geocachingandroidcodingexercise2.data.repository.PinProtoRepositoryImpl
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.util.UiEvent
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/* The MapViewModel consists of variables and methods that handle all the business logic and
states associated with the Google map view.
*/
@ExperimentalPermissionsApi
@HiltViewModel
class MapViewModel @Inject constructor(
    private val pinProtoRepositoryImpl: PinProtoRepositoryImpl
) : ViewModel() {

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

    fun onEventMapView(event: MapViewEvent) {
        when (event) {
            /* Event is triggered when user clicks the fab button, which then updates the pin
            location saved in the DataStore so that we can mark the map with pinned location even
            after the app is closed. This event also emits the user's location to be used to update
            the pin's location.
             */
            is MapViewEvent.PinUserLocation -> {
                viewModelScope.launch {
                    pinProtoRepositoryImpl.updatePinnedLocation(
                        LatLng(
                            userCurrentLat.value,
                            userCurrentLng.value
                        )
                    )
                    _eventFlow.emit(
                        UiEvent.PinCurrentUserLocation(
                            LatLng(
                                userCurrentLat.value,
                                userCurrentLng.value
                            )
                        )
                    )
                }
            }
            /* Event is triggered when user clicks the zoom button within the toolbar, which then
            emits the user's location to update the camera view of the map to focus on said location.
             */
            is MapViewEvent.ZoomUserLocation -> {
                viewModelScope.launch {
                    _eventFlow.emit(
                        UiEvent.ZoomToUserLocation(
                            LatLng(
                                userCurrentLat.value,
                                userCurrentLng.value
                            )
                        )
                    )
                }
            }
            /* Event is triggered when the Google map view is loaded, which then retrieves the saved
            pin location in the DataStore. This event also emits pin location to update the current
            pin location state and place a marker on the map.
             */
            is MapViewEvent.PinLoaded -> {
                viewModelScope.launch {
                    val pin = pinProtoRepositoryImpl.getPinnedLocation()
                    if (pin.latitude != 0.0 && pin.longitude != 0.0) {
                        _eventFlow.emit(
                            UiEvent.PinSavedLocation(
                                LatLng(
                                    pin.latitude,
                                    pin.longitude
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    /* Method uses Google fused location provider API to request the last know location of the
    user's device. Once the request is successful, the last know location coordinates are used to
    update the map view with the user's location.
     */
    fun initializeDeviceLocation(
        permissions: MultiplePermissionsState,
        context: Context
    ) {
        checkSelfPermission(context)

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

    // This method requests periodical updates of user location
    private fun startLocationUpdates(
        fusedLocationProviderClient: FusedLocationProviderClient,
        permissions: MultiplePermissionsState,
        context: Context
    ) {
        checkSelfPermission(context)

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

    private fun checkSelfPermission(context: Context): Boolean {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }

    /* Method is used to take a image vector and convert it into a BitmapDescriptor to be used in
    Google map view.
     */
    fun bitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        // below line is use to generate a drawable.
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)

        // below line is use to set bounds to our vector drawable.
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )

        // below line is use to create a bitmap for our
        // drawable which we have added.
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        // below line is use to add bitmap in our canvas.
        val canvas = Canvas(bitmap)

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas)

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

}