package com.eynnzerr.bandoristation.usecase.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.eynnzerr.bandoristation.usecase.base.UseCase
import com.eynnzerr.bandoristation.model.UseCaseResult
import kotlinx.coroutines.CoroutineDispatcher

/**
 * 通用的设置偏好项UseCase
 */
class SetPreferenceUseCase(
    private val dataStore: DataStore<Preferences>,
    private val dispatcher: CoroutineDispatcher
) : UseCase<SetPreferenceUseCase.Params, Boolean, Exception>(dispatcher) {
    
    data class Params(
        val key: Preferences.Key<*>,
        val value: Any
    )
    
    @Suppress("UNCHECKED_CAST")
    override suspend fun execute(parameters: Params): UseCaseResult<Boolean, Exception> {
        return try {
            dataStore.edit { preferences ->
                when (val value = parameters.value) {
                    is String -> preferences[parameters.key as Preferences.Key<String>] = value
                    is Int -> preferences[parameters.key as Preferences.Key<Int>] = value
                    is Boolean -> preferences[parameters.key as Preferences.Key<Boolean>] = value
                    is Float -> preferences[parameters.key as Preferences.Key<Float>] = value
                    is Long -> preferences[parameters.key as Preferences.Key<Long>] = value
                    is Double -> preferences[parameters.key as Preferences.Key<Double>] = value
                    is Set<*> -> {
                        if (value.all { it is String }) {
                            preferences[parameters.key as Preferences.Key<Set<String>>] = value as Set<String>
                        } else {
                            throw IllegalArgumentException("Unsupported set type")
                        }
                    }
                    else -> throw IllegalArgumentException("Unsupported type")
                }
            }
            UseCaseResult.Success(true)
        } catch (e: Exception) {
            UseCaseResult.Error(e)
        }
    }
}