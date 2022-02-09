package com.github.kuya32.geocachingandroidcodingexercise2.presentation.map

// Class holds the events used in the map view triggered by the user.
sealed class MapViewEvent {
    object PinUserLocation : MapViewEvent()
    object ZoomUserLocation : MapViewEvent()
    object PinLoaded : MapViewEvent()
}
