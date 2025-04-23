package com.eynnzerr.bandoristation.business.social

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.eynnzerr.bandoristation.business.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.social.FollowUserResult
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class FollowUserUseCase(
    private val repository: AppRepository,
    private val dispatcher: CoroutineDispatcher,
    private val dataStore: DataStore<Preferences>,
): UseCase<Long, String, String>(dispatcher) {

    override suspend fun execute(parameters: Long): UseCaseResult<String, String> {
        val (token, followingList) = dataStore.data.map { p ->
            (p[PreferenceKeys.USER_TOKEN] ?: "") to (p[PreferenceKeys.FOLLOWING_LIST] ?: emptySet())
        }.first()
        val follow = parameters.toString() !in followingList

        repository.sendAuthenticHttpsRequest(
            request = ApiRequest.FollowUser(
                id = parameters,
                follow = follow,
            ),
            token = token
        ).handle(
            onSuccess = { responseContent ->
                val followResult = NetResponseHelper.parseApiResponse<FollowUserResult>(responseContent)
                return followResult?.let {
                    dataStore.edit { p ->
                        val newFollowingSet = followingList.toMutableSet().apply {
                            if (follow) add(parameters.toString()) else remove(parameters.toString())
                        }
                        p[PreferenceKeys.FOLLOWING_LIST] = newFollowingSet
                    }
                    UseCaseResult.Success("${if (follow) "关注" else "取关"}成功")
                } ?: UseCaseResult.Error("Failed to parse Signup response.")
            },
            onFailure = {
                return UseCaseResult.Error(it)
            }
        )
    }
}