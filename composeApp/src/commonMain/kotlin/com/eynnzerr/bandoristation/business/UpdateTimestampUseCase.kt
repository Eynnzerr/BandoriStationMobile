package com.eynnzerr.bandoristation.business

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.eynnzerr.bandoristation.business.base.FlowUseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock.System
import kotlinx.serialization.Serializable

class UpdateTimestampUseCase(
    dispatcher: CoroutineDispatcher,
    private val repository: AppRepository,
    private val dataStore: DataStore<Preferences>,
) : FlowUseCase<Unit, Long, Long>(dispatcher) {

    override fun execute(parameters: Unit): Flow<UseCaseResult<Long, Long>> {
        return flow {
            var serverCurrentTime = repository.listenWebSocketForActions(listOf("sendServerTime"))
                .map { response ->
                    NetResponseHelper.parseWebSocketResponse<TimestampWrapper>(response)?.time ?: System.now().toEpochMilliseconds()
                }.first()

            dataStore.edit { p -> p[PreferenceKeys.SERVER_TIME] = serverCurrentTime }

            while (true) {
                // AppLogger.d("UpdateTimestampUseCase", "emitting server time: $serverCurrentTime")
                emit(UseCaseResult.Success(serverCurrentTime))
                delay(1000)
                serverCurrentTime += 1000
            }
        }
    }
}

@Serializable
data class TimestampWrapper(
    val time: Long,
)