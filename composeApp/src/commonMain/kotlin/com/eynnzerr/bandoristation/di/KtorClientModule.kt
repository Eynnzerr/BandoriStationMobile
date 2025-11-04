package com.eynnzerr.bandoristation.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

fun provideKtorClientModule() = module {
    single {
        HttpClient {
//            install(Logging) {
//                logger = Logger.DEFAULT
//                level = LogLevel.HEADERS
//            }
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 5)
                retryOnException(maxRetries = 5)
                exponentialDelay()
            }
            install(ContentNegotiation) {
                json(
                    json = get(),
                    contentType = ContentType.Any
                )
            }
            install(WebSockets) {
                pingIntervalMillis = 20_000
                contentConverter = KotlinxWebsocketSerializationConverter(Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                })
            }
        }
    }
}