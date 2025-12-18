package com.eynnzerr.bandoristation.usecase.chat_group

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.chat_group.SendChatMessageRequest
import com.eynnzerr.bandoristation.preferences.readBasicUserInfo
import com.eynnzerr.bandoristation.usecase.base.UseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

class SendChatGroupMessageUseCase(
    private val repository: AppRepository,
    private val dataStore: DataStore<Preferences>,
): UseCase<String, Unit, Unit>(Dispatchers.IO) {

    override suspend fun execute(parameters: String): UseCaseResult<Unit, Unit> {
        val (_, name, avatar) = dataStore.readBasicUserInfo()

        val request = SendChatMessageRequest(
            content = parameters,
            username = name,
            avatar = avatar
        )
        repository.sendChatGroupMessage(request)
        return UseCaseResult.Success(Unit)
    }
}