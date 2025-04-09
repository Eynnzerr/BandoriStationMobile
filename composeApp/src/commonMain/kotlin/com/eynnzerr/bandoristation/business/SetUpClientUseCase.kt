package com.eynnzerr.bandoristation.business

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.eynnzerr.bandoristation.business.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.model.ClientSetInfo
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

class SetUpClientUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
    private val dataStore: DataStore<Preferences>,
) : UseCase<ClientSetInfo, Unit, Unit>(dispatcher) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override suspend fun execute(parameters: ClientSetInfo): UseCaseResult<Unit, Unit> {
        // connect websocket and then send setClient+setAccessPermission
        with(repository) {
            scope.launch {
                connectWebSocket()
            }

            setWebSocketApiClient(parameters)

            val token = dataStore.data.map { p -> p[PreferenceKeys.USER_TOKEN] ?: testToken }.first()
            setAccessPermission(token)
        }

        return UseCaseResult.Success(Unit) // WebSocket has no sync response so just return Unit.
    }
}