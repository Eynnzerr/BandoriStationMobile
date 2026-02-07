package com.eynnzerr.bandoristation.usecase.time

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import com.eynnzerr.bandoristation.usecase.base.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.concurrent.Volatile
import kotlin.time.Clock

class UpdateTimestampUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val repository: AppRepository,
    private val dataStore: DataStore<Preferences>,
) : FlowUseCase<Unit, Long, Long>(dispatcher) {

    @Volatile
    private var serverTimeAnchor: Long = 0L

    @Volatile
    private var localTimeAnchor: Long = 0L

    init {
        CoroutineScope(dispatcher).launch {
            repository.listenWebSocketForActions(listOf("sendServerTime"))
                .collect { response ->
                    val serverTime = NetResponseHelper.parseWebSocketResponse<TimestampWrapper>(response)?.time
                    if (serverTime != null) {
                        val now = Clock.System.now().toEpochMilliseconds()
                        serverTimeAnchor = serverTime
                        localTimeAnchor = now
                        dataStore.edit { p -> p[PreferenceKeys.SERVER_TIME] = serverTime }
                    }
                }
        }
    }

    override fun execute(parameters: Unit): Flow<UseCaseResult<Long, Long>> {
        return flow {
            while (true) {
                if (serverTimeAnchor == 0L) {
                    delay(200L)
                    continue
                }
                val now = Clock.System.now().toEpochMilliseconds()
                val elapsed = (now - localTimeAnchor).coerceAtLeast(0L)
                emit(UseCaseResult.Success(serverTimeAnchor + elapsed))
                delay(1000L)
            }
        }
    }
}

@Serializable
data class TimestampWrapper(
    val time: Long,
)
