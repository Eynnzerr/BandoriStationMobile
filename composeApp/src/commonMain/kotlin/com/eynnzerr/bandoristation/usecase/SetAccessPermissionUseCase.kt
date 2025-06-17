package com.eynnzerr.bandoristation.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.eynnzerr.bandoristation.usecase.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SetAccessPermissionUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
    private val dataStore: DataStore<Preferences>,
): UseCase<String?, Unit, Unit>(dispatcher) {
    override suspend fun execute(parameters: String?): UseCaseResult<Unit, Unit> {
        val token = parameters ?: dataStore.data.map { p -> p[PreferenceKeys.USER_TOKEN] ?: "" }.first()
        repository.setAccessPermission(token)

        return UseCaseResult.Success(Unit) // WebSocket has no sync response so just return Unit.
    }
}