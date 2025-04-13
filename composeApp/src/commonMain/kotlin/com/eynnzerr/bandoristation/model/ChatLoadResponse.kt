package com.eynnzerr.bandoristation.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param messageList 当前加载批次的聊天消息列表
 * @param selfId 自身用户ID（仅在初始化时返回有效值）
 * @param isEnd 消息是否加载完全
 */
@Serializable
data class ChatLoadResponse(
    @SerialName("message_list") val messageList: List<ChatMessage> = emptyList(),
    @SerialName("self_id") val selfId: Long = 0,
    @SerialName("is_end") val isEnd: Boolean = true,
)