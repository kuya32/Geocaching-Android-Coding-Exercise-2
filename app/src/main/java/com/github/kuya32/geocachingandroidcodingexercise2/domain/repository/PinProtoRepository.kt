package com.github.kuya32.geocachingandroidcodingexercise2.domain.repository

import com.github.kuya32.geocachingandroidcodingexercise2.Pin
import com.google.android.gms.maps.model.LatLng

interface PinProtoRepository {
    suspend fun updatePinnedLocation(pin: LatLng)
    suspend fun getPinnedLocation(): Pin
}