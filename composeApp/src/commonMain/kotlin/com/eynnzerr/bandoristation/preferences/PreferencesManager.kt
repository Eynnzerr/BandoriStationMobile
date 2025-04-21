package com.eynnzerr.bandoristation.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object PreferencesManager : KoinComponent {
    private val dataStore : DataStore<Preferences> by inject()

    fun preferencesFlow() = dataStore.data
}