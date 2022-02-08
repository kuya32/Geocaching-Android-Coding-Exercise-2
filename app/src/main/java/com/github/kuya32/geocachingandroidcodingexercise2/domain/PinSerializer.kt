package com.github.kuya32.geocachingandroidcodingexercise2.domain

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.github.kuya32.geocachingandroidcodingexercise2.Pin
import com.github.kuya32.geocachingandroidcodingexercise2.utils.Constants
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

// Tells DataStore how to read and write the Pin data type
object PinSerializer : Serializer<Pin> {

    override val defaultValue: Pin = Pin.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Pin {
        try {
            return Pin.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: Pin, output: OutputStream) = t.writeTo(output)

    val Context.pinDataStore: DataStore<Pin> by dataStore(
        fileName = Constants.PIN_DATA_STORE_FILE_NAME,
        serializer = PinSerializer
    )
}