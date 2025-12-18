package com.eynnzerr.bandoristation.usecase.chat_group

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.NetworkUrl
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.chat_group.ChatGroupSyncData
import com.eynnzerr.bandoristation.preferences.readBasicUserInfo
import com.eynnzerr.bandoristation.preferences.readEncryptionToken
import com.eynnzerr.bandoristation.usecase.base.UseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

//class CheckChatGroupUseCase(
//    private val repository: AppRepository,
//    private val dataStore: DataStore<Preferences>,
//): UseCase<String, String, String>(Dispatchers.IO) {
//
//    override suspend fun execute(parameters: String): UseCaseResult<String, String> {
//        val token = dataStore.readEncryptionToken()
//
//        repository.sendEncryptionRequest(
//            path = NetworkUrl.GET_GROUP,
//            request = TODO(),
//            token = token,
//        ).handle(
//            onSuccess = { response ->
//                val wrapper = NetResponseHelper.parseApiResponse<>(response)
//                return wrapper?.let {
//                    UseCaseResult.Success(it)
//                } ?: UseCaseResult.Error("Failed to parse response.")
//            },
//            onFailure = {
//                return UseCaseResult.Error(it)
//            }
//        )
//    }
//}