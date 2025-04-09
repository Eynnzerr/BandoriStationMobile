package com.eynnzerr.bandoristation.model

/**
 * A generic class that holds a value with its loading status.
 *
 * @param D The return type of the [UseCaseResult]
 * @param E The return type of the [UseCaseResult] in case of business rule error
 */
sealed class UseCaseResult<out D, out E> {
    data class Success<out D>(val data: D) : UseCaseResult<D, Nothing>()
    data class Error<out E>(val error: E) : UseCaseResult<Nothing, E>()
    data object Loading : UseCaseResult<Nothing, Nothing>()

    fun isSuccessful() = this is Success
    fun hasFailed() = this is Error<*>
    fun isLoading() = this is Loading

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error<*> -> "Error[error=$error]"
            Loading -> "Loading"
        }
    }
}