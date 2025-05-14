package com.eynnzerr.bandoristation.business

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.eynnzerr.bandoristation.business.base.FlowUseCase
import com.eynnzerr.bandoristation.business.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.WebSocketClient
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import com.eynnzerr.bandoristation.utils.testToken
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ConnectWebSocketUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
): FlowUseCase<Unit, WebSocketClient.ConnectionState, Unit>(dispatcher) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    override fun execute(parameters: Unit): Flow<UseCaseResult<WebSocketClient.ConnectionState, Unit>> {
        // Connect to WebSocket server，and listen for webSocket connection state. Only called once when app starts.
        scope.launch {
            repository.connectWebSocket()
        }

        return repository.listenWebSocketConnection().map { connectionState ->
            UseCaseResult.Success(connectionState)
        }
    }
}