package com.github.kuya32.geocachingandroidcodingexercise2.presentation.map

sealed class MapViewEvent {
    object PinUserLocation : MapViewEvent()
    object ZoomUserLocation : MapViewEvent()
}
