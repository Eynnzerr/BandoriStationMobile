package com.eynnzerr.bandoristation.usecase.social

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.eynnzerr.bandoristation.usecase.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class InformUserUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
    private val dataStore: DataStore<Preferences>,
): UseCase<ApiRequest.InformUser, Unit, String>(dispatcher) {

    override suspend fun execute(parameters: ApiRequest.InformUser): UseCaseResult<Unit, String> {
        val token = dataStore.data.map { p -> p[PreferenceKeys.USER_TOKEN] ?: "" }.first()

        repository.sendAuthenticHttpsRequest(
            request = parameters,
            token = token
        ).handle(
            onSuccess = {
                return UseCaseResult.Success(Unit)
            },
            onFailure = {
                return UseCaseResult.Error(it)
            }
        )
    }
}