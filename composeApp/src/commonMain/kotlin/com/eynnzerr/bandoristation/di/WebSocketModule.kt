package com.eynnzerr.bandoristation.di

import com.eynnzerr.bandoristation.data.remote.NetworkUrl
import com.eynnzerr.bandoristation.data.remote.websocket.WebSocketClient
import org.koin.dsl.module

fun provideWebSocketClientModule() = module {
    single {
        WebSocketClient(
            serverUrl = NetworkUrl.WS_SERVER,
            client = get(),
            json = get(),
        )
    }
}