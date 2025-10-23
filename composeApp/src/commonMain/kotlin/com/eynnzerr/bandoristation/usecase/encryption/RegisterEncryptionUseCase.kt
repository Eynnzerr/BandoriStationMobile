package com.eynnzerr.bandoristation.usecase.encryption

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.NetworkUrl
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.data.remote.websocket.WebSocketClient
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import com.eynnzerr.bandoristation.usecase.base.UseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable

class RegisterEncryptionUseCase(
    private val repository: AppRepository,
    private val encryptionSocketClient:  WebSocketClient,
    private val dataStore: DataStore<Preferences>,
): UseCase<Unit, String, String>(Dispatchers.IO) {

    override suspend fun execute(parameters: Unit): UseCaseResult<String, String> {
        val (userId, token) = dataStore.data.map { p ->
            (p[PreferenceKeys.USER_ID] ?: 0) to (p[PreferenceKeys.USER_TOKEN] ?: "")
        }.first()

        repository.sendEncryptionRequest(
            path = NetworkUrl.REGISTER,
            request = ApiRequest.RegisterEncryptionRequest(userId.toString(), token)
        ).handle(
            onSuccess = { response ->
                val wrapper = NetResponseHelper.parseApiResponse<UserRegisterResponse>(response)
                return wrapper?.let { w ->
                    dataStore.edit { p -> p[PreferenceKeys.ENCRYPTION_TOKEN] = w.token }
                    encryptionSocketClient.connect() // try to connect for the first time.
                    UseCaseResult.Success(w.token)
                } ?:UseCaseResult.Error("Failed to parse getRoomNumberFilterResponse.")
            },
            onFailure = {
                return UseCaseResult.Error(it)
            }
        )
    }
}

@Serializable
data class UserRegisterResponse(
    val token: String,
    val expiresIn: Long
)