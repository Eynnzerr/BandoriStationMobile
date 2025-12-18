package com.eynnzerr.bandoristation.usecase.chat_group

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.NetworkUrl
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.preferences.readBasicUserInfo
import com.eynnzerr.bandoristation.preferences.readEncryptionToken
import com.eynnzerr.bandoristation.usecase.base.UseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.serialization.Serializable

class CreateChatGroupUseCase(
    private val repository: AppRepository,
    private val dataStore: DataStore<Preferences>,
): UseCase<String, String, String>(Dispatchers.IO) {

    override suspend fun execute(parameters: String): UseCaseResult<String, String> {
        val token = dataStore.readEncryptionToken()
        val (_, username, avatar) = dataStore.readBasicUserInfo()

        repository.sendEncryptionRequest(
            path = NetworkUrl.CREATE_CHAT,
            request = ApiRequest.CreateChatRequest(
                roomName = parameters,
                ownerName = username,
                ownerAvatar = avatar,
            ),
            token = token,
        ).handle(
            onSuccess = { response ->
                val wrapper = NetResponseHelper.parseApiResponse<CreateChatResponse>(response)
                return wrapper?.let {
                    UseCaseResult.Success(it.groupId)
                } ?: UseCaseResult.Error("Failed to parse ${NetworkUrl.CREATE_CHAT} response.")
            },
            onFailure = {
                return UseCaseResult.Error(it)
            }
        )
    }
}

@Serializable
data class CreateChatResponse(
    val groupId: String
)