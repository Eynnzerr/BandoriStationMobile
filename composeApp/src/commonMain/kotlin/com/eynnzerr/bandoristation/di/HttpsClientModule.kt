package com.eynnzerr.bandoristation.di

import com.eynnzerr.bandoristation.data.remote.NetworkUrl
import com.eynnzerr.bandoristation.data.remote.https.HttpsClient
import org.koin.dsl.module

fun provideHttpsClientModule() = module {
    single {
        HttpsClient(
            apiUrl = NetworkUrl.API_SERVER,
            httpsUrl = NetworkUrl.HTTPS_SERVER,
            client = get(),
            json = get(),
        )
    }
}