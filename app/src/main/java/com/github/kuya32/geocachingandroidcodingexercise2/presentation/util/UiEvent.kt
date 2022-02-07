package com.github.kuya32.geocachingandroidcodingexercise2.presentation.util

import android.content.Intent
import androidx.compose.runtime.MutableState
import com.google.android.gms.maps.model.LatLng

sealed class UiEvent {
    data class LaunchPermissions(val permissionLaunched: Boolean): UiEvent()
    data class NavigateSettings(val settingsLaunched: Boolean): UiEvent()
    data class NavigateToUserLocation(val userLocation: LatLng): UiEvent()
}
