package com.eynnzerr.bandoristation.usecase.encryption

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.NetworkUrl
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import com.eynnzerr.bandoristation.usecase.base.UseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable

class GetBlackWhiteListUseCase(
    private val repository: AppRepository,
    private val dataStore: DataStore<Preferences>,
): UseCase<Unit, ListResponse, String>(Dispatchers.IO) {

    override suspend fun execute(parameters: Unit): UseCaseResult<ListResponse, String> {
        val token = dataStore.data.map { p -> p[PreferenceKeys.ENCRYPTION_TOKEN] ?: "" }.first()

        repository.sendEncryptionRequest(
            path = NetworkUrl.GET_LISTS,
            request = ApiRequest.Empty(),
            token = token,
        ).handle(
            onSuccess = { response ->
                val wrapper = NetResponseHelper.parseApiResponse<ListResponse>(response)
                return wrapper?.let { UseCaseResult.Success(it) } ?: UseCaseResult.Error("Failed to parse ListResponse.")
            },
            onFailure = {
                return UseCaseResult.Error(it)
            }
        )
    }
}

@Serializable
data class ListResponse(
    val blacklist: List<String>,
    val whitelist: List<String>,
)
