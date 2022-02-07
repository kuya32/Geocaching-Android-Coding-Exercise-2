package com.github.kuya32.geocachingandroidcodingexercise2.presentation.map

sealed class MapViewEvent {
    object PinRemoveCurrentLocation: MapViewEvent()
    object ZoomPinnedLocation: MapViewEvent()
    object ZoomUserLocation: MapViewEvent()
    object CalculatedDistance: MapViewEvent()
}
