package com.github.kuya32.geocachingandroidcodingexercise2.domain

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.github.kuya32.geocachingandroidcodingexercise2.Pin
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object PinSerializer: Serializer<Pin> {

    override val defaultValue: Pin = Pin.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Pin {
        try {
            return Pin.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: Pin, output: OutputStream) = t.writeTo(output)
}