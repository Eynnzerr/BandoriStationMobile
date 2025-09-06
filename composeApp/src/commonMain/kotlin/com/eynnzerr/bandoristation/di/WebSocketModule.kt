package com.eynnzerr.bandoristation.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.eynnzerr.bandoristation.data.remote.NetworkUrl
import com.eynnzerr.bandoristation.data.remote.websocket.WebSocketClient
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun provideWebSocketClientModule() = module {
    single(named("BandoriStationWS")) {
        WebSocketClient(
            serverUrl = NetworkUrl.WS_SERVER,
            needHeartbeat = true,
            client = get(),
            json = get(),
            tokenProvider = null
        )
    }

    single(named("BandoriscriptionWS")) {
        val dataStore: DataStore<Preferences> = get()
        WebSocketClient(
            serverUrl = NetworkUrl.ENCRYPTION_WS,
            needHeartbeat = false,
            client = get(),
            json = get(),
            tokenProvider = { dataStore.data.map { p -> p[PreferenceKeys.ENCRYPTION_TOKEN] ?: "" }.first() }
        )
    }
}