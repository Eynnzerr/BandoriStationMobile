package com.eynnzerr.bandoristation.business

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.eynnzerr.bandoristation.business.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import com.eynnzerr.bandoristation.utils.testToken
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ConnectWebSocketUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
    private val dataStore: DataStore<Preferences>,
): UseCase<Unit, Unit, Unit>(dispatcher) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Connect to WebSocket server，and set client permission using token. Only called once when app starts.
    override suspend fun execute(parameters: Unit): UseCaseResult<Unit, Unit> {
        with(repository) {
            scope.launch {
                connectWebSocket()
            }

            // TODO token失效时返回错误
            val token = dataStore.data.map { p -> p[PreferenceKeys.USER_TOKEN] ?: "" }.first()
            setAccessPermission(token)
        }

        return UseCaseResult.Success(Unit) // WebSocket has no sync response so just return Unit.
    }
}