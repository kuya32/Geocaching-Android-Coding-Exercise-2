package com.github.kuya32.geocachingandroidcodingexercise2.presentation.util

import android.content.Intent
import androidx.compose.runtime.MutableState
import com.google.android.gms.maps.model.LatLng

sealed class UiEvent {
    data class LaunchPermissions(val permissionLaunched: Boolean): UiEvent()
    data class NavigateSettings(val settingsLaunched: Boolean): UiEvent()
    data class PinCurrentUserLocation(val pinnedLocation: LatLng): UiEvent()
    data class UpdatePinnedLocation(val newPinnedLocation: LatLng): UiEvent()
    data class ZoomToPinLocation(val pinLocation: LatLng): UiEvent()
    data class ZoomToUserLocation(val userLocation: LatLng): UiEvent()
}
