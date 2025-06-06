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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock.System
import kotlinx.serialization.Serializable
import kotlin.concurrent.Volatile

class UpdateTimestampUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val repository: AppRepository,
    private val dataStore: DataStore<Preferences>,
) : FlowUseCase<Unit, Long, Long>(dispatcher) {

    @Volatile
    private var serverCurrentTime: Long = 0L

    init {
        CoroutineScope(dispatcher).launch {
            repository.listenWebSocketForActions(listOf("sendServerTime"))
                .map { response ->
                    NetResponseHelper.parseWebSocketResponse<TimestampWrapper>(response)?.time
                }
                .onEach { newTime ->
                    newTime?.let {
                        // plus one to prevent possible negative time
                        serverCurrentTime = it + 1
                        dataStore.edit { p -> p[PreferenceKeys.SERVER_TIME] = it + 1 }
                    }
                }
                .launchIn(this)
        }
    }

    override fun execute(parameters: Unit): Flow<UseCaseResult<Long, Long>> {
        return flow {
            if (serverCurrentTime == 0L) {
                 delay(200)
            }

            // fallback
            if (serverCurrentTime == 0L) {
                serverCurrentTime = System.now().toEpochMilliseconds()
            }

            var lastEmittedTime = serverCurrentTime
            while (true) {
                if (serverCurrentTime > lastEmittedTime) {
                    val offset = (System.now().toEpochMilliseconds() - serverCurrentTime) % 1000
                    lastEmittedTime = serverCurrentTime + offset
                }
                emit(UseCaseResult.Success(lastEmittedTime))
                delay(1000)
                lastEmittedTime += 1000
            }
        }
    }
}

@Serializable
data class TimestampWrapper(
    val time: Long,
)
