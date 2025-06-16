package com.eynnzerr.bandoristation.usecase.account

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.eynnzerr.bandoristation.usecase.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class LogoutUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
    private val dataStore: DataStore<Preferences>,
): UseCase<Unit, Unit, String>(dispatcher) {

    override suspend fun execute(parameters: Unit): UseCaseResult<Unit, String> {
        val token = dataStore.data.map { p -> p[PreferenceKeys.USER_TOKEN] ?: "" }.first()

        repository.sendAuthenticHttpsRequest(
            request = ApiRequest.Logout(),
            token = token
        ).handle(
            onSuccess = {
                // when success, response returns "" so no need to processing.
                dataStore.edit { p ->
                    p[PreferenceKeys.USER_TOKEN] = ""
                    p[PreferenceKeys.FOLLOWING_LIST] = emptySet()
                }
                return UseCaseResult.Success(Unit)
            },
            onFailure = {
                return UseCaseResult.Error(it)
            }
        )
    }
}