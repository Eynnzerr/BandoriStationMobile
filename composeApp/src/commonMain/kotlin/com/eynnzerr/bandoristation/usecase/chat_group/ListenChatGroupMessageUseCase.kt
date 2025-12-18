package com.eynnzerr.bandoristation.usecase.chat_group

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.websocket.EncryptionSocketActions
import com.eynnzerr.bandoristation.data.remote.websocket.NetResponseHelper
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.chat_group.ChatGroupMessage
import com.eynnzerr.bandoristation.model.chat_group.GroupMessageType
import com.eynnzerr.bandoristation.model.chat_group.NewOwnerPayload
import com.eynnzerr.bandoristation.model.chat_group.UserChatPayload
import com.eynnzerr.bandoristation.preferences.readUserIdAsString
import com.eynnzerr.bandoristation.usecase.base.FlowUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ListenChatGroupMessageUseCase(
    private val repository: AppRepository,
    private val dataStore: DataStore<Preferences>,
) : FlowUseCase<Unit, ChatGroupMessage, Unit>(Dispatchers.IO) {

    override fun execute(parameters: Unit): Flow<UseCaseResult<ChatGroupMessage, Unit>> {
        return repository.listenEncryptionSocketForActions(
            listOf(
                EncryptionSocketActions.NEW_CHAT_MESSAGE,
                EncryptionSocketActions.USER_JOINED_CHAT,
                EncryptionSocketActions.USER_LEFT_CHAT,
                EncryptionSocketActions.USER_REMOVED_FROM_CHAT,
                EncryptionSocketActions.NEW_OWNER_ASSIGNED,
                EncryptionSocketActions.OWNER_CHANGED,
            )
        )
            .map { response ->
                when (response.action) {
                    EncryptionSocketActions.NEW_CHAT_MESSAGE -> {
                        val message = NetResponseHelper.parseWebSocketResponse<ChatGroupMessage>(response)
                        message?.let { UseCaseResult.Success(it) } ?: UseCaseResult.Error(Unit)
                    }

                    EncryptionSocketActions.USER_JOINED_CHAT -> {
                        val payload = NetResponseHelper.parseWebSocketResponse<UserChatPayload>(response)
                        payload?.let {
                            val message = ChatGroupMessage(
                                id = 0,
                                senderId = "",
                                content = "${it.user.name} 加入了群聊。",
                                username = "",
                                avatar = "",
                                createdAt = "",
                                type = GroupMessageType.SYSTEM,
                            )

                            UseCaseResult.Success(message)
                        } ?: UseCaseResult.Error(Unit)
                    }

                    EncryptionSocketActions.USER_LEFT_CHAT -> {
                        val payload = NetResponseHelper.parseWebSocketResponse<UserChatPayload>(response)
                        payload?.let {
                            val message = ChatGroupMessage(
                                id = 0,
                                senderId = "",
                                content = "${it.user.name} 离开了群聊。",
                                username = "",
                                avatar = "",
                                createdAt = "",
                                type = GroupMessageType.SYSTEM,
                            )

                            UseCaseResult.Success(message)
                        } ?: UseCaseResult.Error(Unit)
                    }

                    EncryptionSocketActions.USER_REMOVED_FROM_CHAT -> {
                        val selfId = dataStore.readUserIdAsString()
                        val payload = NetResponseHelper.parseWebSocketResponse<UserChatPayload>(response)

                        payload?.let {
                            val message = ChatGroupMessage(
                                id = 0,
                                senderId = "",
                                content = if (selfId == it.user.id) "您已被房主移出群聊。" else "${it.user.name} 被移出了群聊。",
                                username = "",
                                avatar = "",
                                createdAt = "",
                                type = GroupMessageType.SYSTEM,
                            )

                            UseCaseResult.Success(message)
                        } ?: UseCaseResult.Error(Unit)
                    }

                    EncryptionSocketActions.NEW_OWNER_ASSIGNED -> {
                        val selfId = dataStore.readUserIdAsString()
                        val payload = NetResponseHelper.parseWebSocketResponse<NewOwnerPayload>(response)

                        if (payload != null) {
                            val message = ChatGroupMessage(
                                id = 0,
                                senderId = "",
                                content = if (selfId == payload.newOwnerId) "原房主退出群聊，您成为新房主。" else "用户${payload.newOwnerId} 成为新房主。",
                                username = "",
                                avatar = "",
                                createdAt = "",
                                type = GroupMessageType.SYSTEM,
                            )

                            UseCaseResult.Success(message)
                        } else {
                            UseCaseResult.Error(Unit)
                        }
                    }

                    else -> null
                } ?: UseCaseResult.Error(Unit)
            }
    }
}