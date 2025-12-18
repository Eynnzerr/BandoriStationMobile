package com.eynnzerr.bandoristation.usecase.chat_group

import com.eynnzerr.bandoristation.usecase.base.UseCaseAggregator

class ChatGroupAggregator(
    private val getAllChatGroups: GetAllChatGroupsUseCase,
    private val createChatGroup: CreateChatGroupUseCase,
    private val joinChatGroup: JoinChatGroupUseCase,
    private val listenChatGroupChange: ListenChatGroupChangeUseCase,
    private val listenChatGroupSync: ListenChatGroupSyncUseCase,
    private val listenChatGroupMessage: ListenChatGroupMessageUseCase,
    private val sendChatGroupMessage: SendChatGroupMessageUseCase,
    private val leaveChatGroup: LeaveChatGroupUseCase,
    private val removeMember: RemoveMemberUseCase,
    private val listenError: ListenErrorUseCase,
) : UseCaseAggregator {
    suspend fun getAllChatGroups() = getAllChatGroups.invoke(Unit)

    suspend fun createChatGroup(groupName: String) = createChatGroup.invoke(groupName)

    suspend fun joinChatGroup(ownerId: String) = joinChatGroup.invoke(ownerId)

    fun listenChatGroupChange() = listenChatGroupChange.invoke(Unit)

    fun listenChatGroupSync() = listenChatGroupSync.invoke(Unit)

    fun listenChatGroupMessage() = listenChatGroupMessage.invoke(Unit)

    suspend fun sendChatGroupMessage(message: String) = sendChatGroupMessage.invoke(message)

    suspend fun leaveChatGroup() = leaveChatGroup.invoke(Unit)

    suspend fun removeMember(id: String) = removeMember.invoke(id)

    fun listenError() = listenError.invoke(Unit)
}