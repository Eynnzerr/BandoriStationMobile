package com.eynnzerr.bandoristation.usecase.chat_group

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.NetworkUrl
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.preferences.readEncryptionToken
import com.eynnzerr.bandoristation.usecase.base.UseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

class LeaveChatGroupUseCase(
    private val repository: AppRepository,
    private val dataStore: DataStore<Preferences>,
): UseCase<Unit, String, String>(Dispatchers.IO) {

    override suspend fun execute(parameters: Unit): UseCaseResult<String, String> {
        val token = dataStore.readEncryptionToken()

        repository.sendEncryptionRequest(
            path = NetworkUrl.LEAVE_CHAT,
            request = ApiRequest.Empty(),
            token = token,
        ).handle(
            onSuccess = { response ->
                return UseCaseResult.Success("您已离开群聊。")
            },
            onFailure = {
                return UseCaseResult.Error(it)
            }
        )
    }
}