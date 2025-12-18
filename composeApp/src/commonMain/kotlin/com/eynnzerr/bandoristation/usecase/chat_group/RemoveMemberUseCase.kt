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

class RemoveMemberUseCase(
    private val repository: AppRepository,
    private val dataStore: DataStore<Preferences>,
): UseCase<String, String, String>(Dispatchers.IO) {

    override suspend fun execute(parameters: String): UseCaseResult<String, String> {
        val token = dataStore.readEncryptionToken()

        repository.sendEncryptionRequest(
            path = NetworkUrl.REMOVE_MEMBER,
            request = ApiRequest.RemoveMemberRequest(
                userId = parameters
            ),
            token = token,
        ).handle(
            onSuccess = { response ->
                return UseCaseResult.Success("已移除该成员。")
            },
            onFailure = {
                return UseCaseResult.Error(it)
            }
        )
    }
}