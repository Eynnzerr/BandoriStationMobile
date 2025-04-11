package com.eynnzerr.bandoristation.feature.chat

import com.eynnzerr.bandoristation.base.UIEffect
import com.eynnzerr.bandoristation.base.UIEvent
import com.eynnzerr.bandoristation.base.UIState
import com.eynnzerr.bandoristation.model.ChatInitResponse
import com.eynnzerr.bandoristation.model.ChatMessage
import org.jetbrains.compose.resources.StringResource

data class ChatState(
    val chats: List<ChatMessage> = emptyList(),
    val hasMore: Boolean = false,
    val selfId: Long = 0,
    val initialized: Boolean = false,
    val unreadCount: Int = 0,
) : UIState {
    companion object {
        fun initial() = ChatState()
    }
}

sealed class ChatIntent : UIEvent {
    data class LoadInitial(val initialData: ChatInitResponse): ChatIntent()
    data class LoadMore(val chats: List<ChatMessage>): ChatIntent()
    class ClearAll(): ChatIntent()
    data class AppendNewChats(val chats: List<ChatMessage>): ChatIntent()
    data class SendChat(val message: String): ChatIntent()
    class ClearUnreadCount(): ChatIntent()
}

sealed class ChatEffect: UIEffect {
    class ScrollToLatest: ChatEffect()
    data class ShowSnackbar(val textRes: StringResource): ChatEffect()
}