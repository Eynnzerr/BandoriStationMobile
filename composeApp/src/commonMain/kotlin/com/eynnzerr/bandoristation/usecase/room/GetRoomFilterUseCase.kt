package com.eynnzerr.bandoristation.usecase.room

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.eynnzerr.bandoristation.usecase.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.RoomFilter
import com.eynnzerr.bandoristation.model.RoomFilterWrapper
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class GetRoomFilterUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
    private val dataStore: DataStore<Preferences>,
): UseCase<Unit, RoomFilter, String>(dispatcher) {

    override suspend fun execute(parameters: Unit): UseCaseResult<RoomFilter, String> {
        val token = dataStore.data.map { p -> p[PreferenceKeys.USER_TOKEN] ?: "" }.first()

        repository.sendAuthenticHttpsRequest(
            request = ApiRequest.GetRoomFilter(),
            token = token
        ).handle(
            onSuccess = { response ->
                val wrapper = NetResponseHelper.parseApiResponse<RoomFilterWrapper>(response)
                return wrapper?.let { UseCaseResult.Success(it.filter) } ?: UseCaseResult.Error("Failed to parse getRoomNumberFilterResponse.")
            },
            onFailure = {
                return UseCaseResult.Error(it)
            }
        )
    }
}