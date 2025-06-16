package com.eynnzerr.bandoristation.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

expect fun produceDataStorePath(): String

private val dataStoreInstance: DataStore<Preferences> by lazy {
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { produceDataStorePath().toPath() }
    )
}

fun createDataStore(): DataStore<Preferences> = dataStoreInstance

internal const val dataStoreFileName = "bandori_station.preferences_pb"