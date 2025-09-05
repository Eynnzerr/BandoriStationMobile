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

class VerifyInviteCodeUseCase(
    private val repository: AppRepository,
    private val dataStore: DataStore<Preferences>,
): UseCase<ApiRequest.VerifyInviteCodeRequest, String, String>(Dispatchers.IO) {

    override suspend fun execute(parameters: ApiRequest.VerifyInviteCodeRequest): UseCaseResult<String, String> {
        val token = dataStore.data.map { p -> p[PreferenceKeys.ENCRYPTION_TOKEN] ?: "" }.first()

        repository.sendEncryptionRequest(
            path = NetworkUrl.VERIFY_INVITE_CODE,
            request = parameters,
            token = token,
        ).handle(
            onSuccess = { response ->
                val wrapper = NetResponseHelper.parseApiResponse<RoomResponse>(response)
                return wrapper?.let { UseCaseResult.Success(it.number) } ?: UseCaseResult.Error("Failed to parse RoomResponse.")
            },
            onFailure = {
                return UseCaseResult.Error(it)
            }
        )
    }
}

@Serializable
data class RoomResponse(
    val number: String,
)
