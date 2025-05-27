package com.eynnzerr.bandoristation.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
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
//                logger = object : Logger {
//                    override fun log(message: String) {
//                        // 只记录包含 POST 请求信息的日志
//                        if (message.contains("METHOD: POST")) {
//                            AppLogger.d("KtorClient", "send POST request: $message")
//                        }
//                    }
//                }
//                level = LogLevel.ALL  // 记录所有级别的日志
//                sanitizeHeader { header -> false }
//            }
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