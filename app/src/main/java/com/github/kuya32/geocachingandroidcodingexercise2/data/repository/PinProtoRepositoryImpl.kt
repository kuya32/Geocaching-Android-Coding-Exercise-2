package com.github.kuya32.geocachingandroidcodingexercise2.data.repository

import android.content.Context
import com.github.kuya32.geocachingandroidcodingexercise2.Pin
import com.github.kuya32.geocachingandroidcodingexercise2.domain.PinSerializer.pinDataStore
import com.github.kuya32.geocachingandroidcodingexercise2.domain.repository.PinProtoRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.lang.Exception
import javax.inject.Inject

class PinProtoRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
) : PinProtoRepository {
    private val protoDataStore = context.pinDataStore

    // Updates pin coordinates to DataStore
    override suspend fun updatePinnedLocation(pin: LatLng) {
        protoDataStore.updateData { store ->
            store.toBuilder()
                .setLatitude(pin.latitude)
                .setLongitude(pin.longitude)
                .build()
        }
    }

    // Retrieves saved pin coordinates from DataStore
    override suspend fun getPinnedLocation(): Pin {
        return try {
            protoDataStore.data.first()
        } catch (e: Exception) {
            e.printStackTrace()
            Pin.getDefaultInstance()
        }
    }
}