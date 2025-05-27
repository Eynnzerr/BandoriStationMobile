package com.eynnzerr.bandoristation.business.account

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.eynnzerr.bandoristation.business.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.account.UpdateAvatarResult
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UpdateAvatarUseCase(
    private val repository: AppRepository,
    private val dataStore: DataStore<Preferences>,
): UseCase<String, String, String>(Dispatchers.IO) {

    override suspend fun execute(parameters: String): UseCaseResult<String, String> {
        val token = dataStore.data.map { p -> p[PreferenceKeys.USER_TOKEN] ?: "" }.first()

        repository.sendAuthenticHttpsRequest(
            request = ApiRequest.UpdateImage(
                image = parameters,
                type = "avatar",
            ),
            token = token
        ).handle(
            onSuccess = { response ->
                val avatarName = NetResponseHelper.parseApiResponse<UpdateAvatarResult>(response)
                return avatarName?.let { UseCaseResult.Success(it.avatar) } ?: UseCaseResult.Error("Failed to parse UpdateAvatar response.")
            },
            onFailure = {
                return UseCaseResult.Error(it)
            }
        )
    }
}