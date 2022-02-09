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

/* The repository class consist of methods to expose the stored pin data to the rest of the application.
The updatePinnedLocation function updates the pinned coordinate latitude and longitude from the map view
within the DataStore. The getPinnedLocation function retrieves the saved pin coordinates from the DataStore
to place a marker once the map view is loaded.
*/
class PinProtoRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
) : PinProtoRepository {
    private val protoDataStore = context.pinDataStore

    override suspend fun updatePinnedLocation(pin: LatLng) {
        protoDataStore.updateData { store ->
            store.toBuilder()
                .setLatitude(pin.latitude)
                .setLongitude(pin.longitude)
                .build()
        }
    }

    override suspend fun getPinnedLocation(): Pin {
        return try {
            protoDataStore.data.first()
        } catch (e: Exception) {
            e.printStackTrace()
            Pin.getDefaultInstance()
        }
    }
}