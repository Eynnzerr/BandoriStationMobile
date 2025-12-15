package com.eynnzerr.bandoristation.usecase.time

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.eynnzerr.bandoristation.usecase.base.FlowUseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import com.eynnzerr.bandoristation.utils.AppLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
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
                        dataStore.edit { p -> p[PreferenceKeys.SERVER_TIME] = it + 1 }
                    }
                }
                .launchIn(this)
        }

        CoroutineScope(dispatcher).launch {
            dataStore.data.map { p -> p[PreferenceKeys.SERVER_TIME] ?: Clock.System.now().toEpochMilliseconds() }.collect {
                serverCurrentTime = it
            }
        }
    }

    override fun execute(parameters: Unit): Flow<UseCaseResult<Long, Long>> {
        return flow {
            if (serverCurrentTime == 0L) {
                 delay(200)
            }

            // fallback
            if (serverCurrentTime == 0L) {
                AppLogger.d(TAG, "cannot receive serverCurrentTime. Fallback.")
                serverCurrentTime = Clock.System.now().toEpochMilliseconds()
            }

            var lastEmittedTime = serverCurrentTime
            while (true) {
                if (serverCurrentTime > lastEmittedTime) {
                    val offset = (Clock.System.now().toEpochMilliseconds() - serverCurrentTime) % 1000
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

private const val TAG = "UpdateTimestampUseCase"