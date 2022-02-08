package com.github.kuya32.geocachingandroidcodingexercise2.domain

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.github.kuya32.geocachingandroidcodingexercise2.Pin
import com.github.kuya32.geocachingandroidcodingexercise2.presentation.util.Constants

object PinDataStore {
    val Context.pinDataStore: DataStore<Pin> by dataStore(
        fileName = Constants.PIN_DATA_STORE_FILE_NAME,
        serializer = PinSerializer
    )
}