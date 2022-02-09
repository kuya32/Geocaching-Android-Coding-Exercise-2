package com.github.kuya32.geocachingandroidcodingexercise2.presentation.util

import com.google.android.gms.maps.model.LatLng

// Events that trigger from user interaction and change the state of the UI.
sealed class UiEvent {
    data class LaunchPermissions(val permissionLaunched: Boolean) : UiEvent()
    data class NavigateSettings(val settingsLaunched: Boolean) : UiEvent()
    data class PinCurrentUserLocation(val pinnedLocation: LatLng) : UiEvent()
    data class ZoomToUserLocation(val userLocation: LatLng) : UiEvent()
    data class PinSavedLocation(val savedLocation: LatLng) : UiEvent()
}
