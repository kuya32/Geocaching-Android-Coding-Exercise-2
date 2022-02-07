package com.github.kuya32.geocachingandroidcodingexercise2.presentation.map

sealed class MapViewEvent {
    object PinCurrentLocation: MapViewEvent()
    object NavigatePinnedLocation: MapViewEvent()
    object NavigateUserLocation: MapViewEvent()
    object CalculatedDistance: MapViewEvent()
}
