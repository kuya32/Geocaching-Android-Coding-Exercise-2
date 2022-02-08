package com.github.kuya32.geocachingandroidcodingexercise2.domain.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import com.github.kuya32.geocachingandroidcodingexercise2.Pin
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface PinProtoRepository {
    suspend fun updatePinnedLocation(pin: LatLng)
    suspend fun getPinnedLocation(): Pin
}