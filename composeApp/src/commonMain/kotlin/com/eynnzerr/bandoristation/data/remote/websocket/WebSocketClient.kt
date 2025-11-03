package com.eynnzerr.bandoristation.data.remote.websocket

import com.eynnzerr.bandoristation.model.WSRequest
import com.eynnzerr.bandoristation.model.WebSocketRequest
import com.eynnzerr.bandoristation.model.WebSocketResponse
import com.eynnzerr.bandoristation.utils.AppLogger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.header
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlin.concurrent.Volatile

class WebSocketClient(
    private val serverUrl: String,
    private val needHeartbeat: Boolean,
    private val client: HttpClient, // Injected
    val json: Json, // Injected
    private val tokenProvider: (suspend () -> String?)? = null,
    private val autoReconnect: Boolean = true,
    private val name: String = "",
) {
    var session: DefaultClientWebSocketSession? = null
    private var scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val connectMutex = Mutex()

    @Volatile
    private var isNormallyDisconnected = false

    // 响应流
    private val _responseFlow = MutableSharedFlow<WebSocketResponse<JsonElement>>(
        replay = 0,
        extraBufferCapacity = 10
    )
    val responseFlow = _responseFlow.asSharedFlow()

    // 连接状态流
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState = _connectionState.asStateFlow()

    private var reconnectAttempts = 0
    private val maxReconnectAttempts = 5
    private val reconnectDelayMillis = 5000L

    suspend fun connect() {
        AppLogger.d(name, "trying to connect to websocket.")

        connectMutex.withLock {
            if (_connectionState.value is ConnectionState.Connected ||
                _connectionState.value is ConnectionState.Connecting) {
                AppLogger.d(name, "connect: Websocket has already been connecting or connected. Return.")
                return
            }
            _connectionState.value = ConnectionState.Connecting
            isNormallyDisconnected = false
        }

        try {
            val token = tokenProvider?.invoke()

            client.webSocket(
                urlString = serverUrl,
                request = {
                    token?.let {
                        header("Authorization", "Bearer $it")
                    }
                }
            ) {
                session = this
                connectMutex.withLock {
                    _connectionState.value = ConnectionState.Connected
                }
                reconnectAttempts = 0
                AppLogger.d(name, "Connected to websocket $serverUrl")

                // 心跳
                val heartbeatJob: Job? = if (needHeartbeat) {
                    scope.launch {
                        while (isActive) {
                            delay(30000) // 心跳包间隔要求为30秒
                            try {
                                sendRequest(
                                    action = "heartbeat",
                                    data = mapOf("client" to "BandoriStation Mobile")
                                )
                            } catch (e: Exception) {
                                AppLogger.d(name, "Failed to send heartbeat packet to $serverUrl!. exception: ${e.message}")
                                break
                            }
                        }
                    }
                } else {
                    null
                }

                try {
                    for (frame in incoming) {
                        when (frame) {
                            is Frame.Text -> {
                                val text = frame.readText()
                                try {
                                    val response = json.decodeFromString<WebSocketResponse<JsonElement>>(text)
                                    AppLogger.d(name, "received from websocket: $text")
                                    _responseFlow.emit(response)
                                } catch (e: Exception) {
                                    AppLogger.d(name, "WebSocketClient Parse Error: $e")
                                }
                            }
                            is Frame.Close -> {
                                AppLogger.d(name, "received close from websocket. Ignore.")
                            }
                            is Frame.Binary -> {
                                AppLogger.d(name, "received binary from websocket. Ignore.")
                            }
                            is Frame.Ping -> {
                                AppLogger.d(name, "received ping from websocket. Ignore.")
                            }
                            is Frame.Pong -> {
                                AppLogger.d(name, "received pong from websocket. Ignore.")
                            }
                            else -> {
                                AppLogger.d(name, "received other type of frame from websocket. Ignore.")
                            }
                        }
                    }
                    AppLogger.d(name, "exit for-loop of connect().")
                } catch (e: Exception) {
                    AppLogger.d(name, "WebSocketClient Websocket Error: $e")
                    e.printStackTrace()
                } finally {
                    AppLogger.d(name, "WebSocketClient enter finally block of connect().")
                    heartbeatJob?.cancel()
                    connectMutex.withLock {
                        _connectionState.value = ConnectionState.Disconnected
                    }

                    // Try to reconnect
                    if (!isNormallyDisconnected && reconnectAttempts < maxReconnectAttempts && autoReconnect) {
                        reconnectAttempts++
                        delay(reconnectDelayMillis)
                        connect()
                    }
                }
            }
        } catch (e: Exception) {
            AppLogger.d(name, "WebSocketClient Connection Error: $e")
            e.printStackTrace()
            connectMutex.withLock {
                _connectionState.value = ConnectionState.Error(e)
            }

            // Try to reconnect
            if (!isNormallyDisconnected && reconnectAttempts < maxReconnectAttempts && autoReconnect) {
                reconnectAttempts++
                delay(reconnectDelayMillis)
                connect()
            }
        }
    }

    suspend inline fun <reified T> sendRequest(action: String, data: T? = null) {
        val request = WebSocketRequest(action, data)
        val jsonString = json.encodeToString(request)
        AppLogger.d("WebSocketClient", "sending to websocket with payload: $jsonString")
        session?.send(Frame.Text(jsonString))
    }

    suspend inline fun <reified T> sendRequestWithRetry(action: String, data: T? = null) {
        var retryCount = 0
        while (connectionState.value !is ConnectionState.Connected) {
            if (retryCount < 5) {
                delay(1000L)
                retryCount += 1
            } else {
                AppLogger.d("WebSocketClient", "Failed to send $action after retrying since websocket is not connected.")
                return
            }
        }

        // AppLogger.d("WebSocketClient", "Ready to send $action. Current retry attempts: $retryCount")
        sendRequest(action, data)
    }

    suspend fun sendRequests(actions: List<WSRequest>) {
        val jsonString = json.encodeToString(actions)
        AppLogger.d("WebSocketClient", "sending to websocket with payload: $jsonString")
        session?.send(Frame.Text(jsonString))
    }

    fun listenForActions(actions: List<String>): Flow<WebSocketResponse<JsonElement>> {
        return responseFlow.filter { it.action in actions }
    }

    fun listenForAll() = responseFlow

    suspend fun disconnect() {
        connectMutex.withLock {
            if (_connectionState.value == ConnectionState.Disconnected && session == null) {
                AppLogger.d(name, "Already disconnected.")
                return
            }
            _connectionState.value = ConnectionState.Disconnected
        }

        isNormallyDisconnected = true

        if (session != null) {
            try {
                session?.close()
                AppLogger.d(name, "Sent close frame to websocket $serverUrl")
            } catch (e: Exception) {
                AppLogger.d(name, "Error while closing websocket session: ${e.message}")
            } finally {
                session = null
            }
        }

        scope.cancel()
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        AppLogger.d(name, "Disconnected from websocket $serverUrl")
    }

    sealed class ConnectionState {
        data object Disconnected : ConnectionState()
        data object Connecting : ConnectionState()
        data object Connected : ConnectionState()
        data class Error(val exception: Exception) : ConnectionState()
    }
}