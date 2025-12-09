package com.eynnzerr.bandoristation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.eynnzerr.bandoristation.data.remote.websocket.WebSocketClient
import com.eynnzerr.bandoristation.di.DispatcherQualifiers
import com.eynnzerr.bandoristation.model.ClientSetInfo
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.usecase.SetAccessPermissionUseCase
import com.eynnzerr.bandoristation.usecase.SetUpClientUseCase
import com.eynnzerr.bandoristation.usecase.clientName
import com.eynnzerr.bandoristation.usecase.websocket.GetWebSocketStateUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

class ConnectionHandler(
    private val getWebSocketStateUseCase: GetWebSocketStateUseCase,
    private val setAccessPermissionUseCase: SetAccessPermissionUseCase,
    private val setUpClientUseCase: SetUpClientUseCase,
    private val externalScope: CoroutineScope
) {
    fun start() {
        externalScope.launch {
            getWebSocketStateUseCase(Unit).collect { result ->
                if (result is UseCaseResult.Success) {
                    if (result.data is WebSocketClient.ConnectionState.Connected) {
                        setAccessPermissionUseCase(null)
                        setUpClientUseCase(
                            ClientSetInfo(
                                client = clientName,
                                sendRoomNumber = true,
                                sendChat = true,
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StartConnectionHandler() {
    val handler: ConnectionHandler = koinInject()
    LaunchedEffect(Unit) {
        handler.start()
    }
}
