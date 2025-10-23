package com.eynnzerr.bandoristation.usecase.encryption

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.NetworkUrl
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import com.eynnzerr.bandoristation.usecase.base.UseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AddToWhitelistUseCase(
    private val repository: AppRepository,
    private val dataStore: DataStore<Preferences>,
): UseCase<String, Unit, String>(Dispatchers.IO) {

    override suspend fun execute(parameters: String): UseCaseResult<Unit, String> {
        val token = dataStore.data.map { p -> p[PreferenceKeys.ENCRYPTION_TOKEN] ?: "" }.first()

        repository.sendEncryptionRequest(
            path = NetworkUrl.ADD_WHITELIST,
            request = ApiRequest.WhitelistRequest(allowedUserId = parameters),
            token = token,
        ).handle(
            onSuccess = { response ->
                return UseCaseResult.Success(Unit)
            },
            onFailure = {
                return UseCaseResult.Error(it)
            }
        )
    }
}
