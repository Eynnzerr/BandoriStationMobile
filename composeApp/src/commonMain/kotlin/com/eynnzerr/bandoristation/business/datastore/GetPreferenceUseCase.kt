package com.eynnzerr.bandoristation.business.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.eynnzerr.bandoristation.business.base.FlowUseCase
import com.eynnzerr.bandoristation.model.UseCaseResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * 通用的获取偏好项UseCase
 */
class GetPreferenceUseCase<T>(
    private val dataStore: DataStore<Preferences>,
    private val dispatcher: CoroutineDispatcher
) : FlowUseCase<GetPreferenceUseCase.Params<T>, T?, Exception>(dispatcher) {
    
    data class Params<T>(
        val key: Preferences.Key<T>,
        val defaultValue: T? = null
    )
    
    override fun execute(parameters: Params<T>): Flow<UseCaseResult<T?, Exception>> {
        return dataStore.data
            .map { preferences ->
                val value = preferences[parameters.key] ?: parameters.defaultValue
                UseCaseResult.Success(value)
            }
            .catch { e ->
                UseCaseResult.Error(Exception(e))
            }
    }
}