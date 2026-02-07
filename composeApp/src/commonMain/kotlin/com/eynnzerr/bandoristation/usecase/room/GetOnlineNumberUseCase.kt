package com.eynnzerr.bandoristation.usecase.room

import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.usecase.base.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class GetOnlineNumberUseCase(
    private val repository: AppRepository,
    dispatcher: CoroutineDispatcher,
) : UseCase<Unit, Int, String>(dispatcher) {

    override suspend fun execute(parameters: Unit): UseCaseResult<Int, String> {
        repository.getOnlineNumber().handle(
            onSuccess = { responseContent ->
                val payload = NetResponseHelper.parseApiResponse<OnlineNumberResponse>(responseContent)
                return payload?.let { UseCaseResult.Success(it.onlineNumber) }
                    ?: UseCaseResult.Error("Failed to parse getOnlineNumber response.")
            },
            onFailure = {
                return UseCaseResult.Error(it)
            }
        )
    }
}

@Serializable
private data class OnlineNumberResponse(
    @SerialName("online_number") val onlineNumber: Int,
)
