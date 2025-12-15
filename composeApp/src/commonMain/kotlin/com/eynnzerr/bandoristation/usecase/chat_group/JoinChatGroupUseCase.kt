package com.eynnzerr.bandoristation.usecase.chat_group

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.NetworkUrl
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.chat_group.ChatGroupSyncData
import com.eynnzerr.bandoristation.preferences.readBasicUserInfo
import com.eynnzerr.bandoristation.preferences.readEncryptionToken
import com.eynnzerr.bandoristation.preferences.writeCurrentChatGroupId
import com.eynnzerr.bandoristation.usecase.base.UseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

class JoinChatGroupUseCase(
    private val repository: AppRepository,
    private val dataStore: DataStore<Preferences>,
): UseCase<String, ChatGroupSyncData, String>(Dispatchers.IO) {

    override suspend fun execute(parameters: String): UseCaseResult<ChatGroupSyncData, String> {
        val token = dataStore.readEncryptionToken()
        val (_, username, avatar) = dataStore.readBasicUserInfo()

        repository.sendEncryptionRequest(
            path = NetworkUrl.JOIN_CHAT,
            request = ApiRequest.JoinChatRequest(
                ownerId = parameters,
                username = username,
                avatar = avatar,
            ),
            token = token,
        ).handle(
            onSuccess = { response ->
                val wrapper = NetResponseHelper.parseApiResponse<ChatGroupSyncData>(response)
                return wrapper?.let {
                    dataStore.writeCurrentChatGroupId(it.groupId)
                    UseCaseResult.Success(it)
                } ?: UseCaseResult.Error("Failed to parse ${NetworkUrl.JOIN_CHAT} response.")
            },
            onFailure = {
                return UseCaseResult.Error(it)
            }
        )
    }
}