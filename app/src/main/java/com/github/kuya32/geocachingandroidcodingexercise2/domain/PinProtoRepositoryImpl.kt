package com.github.kuya32.geocachingandroidcodingexercise2.domain

import android.content.Context
import androidx.datastore.core.DataStore
import com.github.kuya32.geocachingandroidcodingexercise2.Pin
import com.github.kuya32.geocachingandroidcodingexercise2.domain.PinDataStore.pinDataStore
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject


class PinProtoRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
): PinProtoRepository  {
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

    override suspend fun deletePinnedLocation() {
        protoDataStore.updateData {
            it.toBuilder().clear().build()
        }
    }

//    override suspend fun getPinnedLocation(): Flow<Pin> {
//        return protoDataStore.data
//            .catch { e ->
//                if (e is IOException) {
//                    emit(Pin.getDefaultInstance())
//                } else {
//                    throw e
//                }
//            }.map { protoBuilder ->
//                println("${protoBuilder.defaultInstanceForType}")
//                protoBuilder.defaultInstanceForType
//            }
//    }

}