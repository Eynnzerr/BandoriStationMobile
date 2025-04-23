package com.eynnzerr.bandoristation.feature.chat

import com.eynnzerr.bandoristation.base.UIEffect
import com.eynnzerr.bandoristation.base.UIEvent
import com.eynnzerr.bandoristation.base.UIState
import com.eynnzerr.bandoristation.feature.account.AccountEffect
import com.eynnzerr.bandoristation.model.ChatLoadResponse
import com.eynnzerr.bandoristation.model.ChatMessage
import com.eynnzerr.bandoristation.model.account.AccountInfo
import com.eynnzerr.bandoristation.navigation.Screen
import org.jetbrains.compose.resources.StringResource

data class ChatState(
    val chats: List<ChatMessage> = emptyList(),
    val hasMore: Boolean = false,
    val selfId: Long = 0,
    val initialized: Boolean = false,
    val unreadCount: Int = 0,
    val isLoading: Boolean = true,
    val selectedUser: AccountInfo = AccountInfo(),
    val followingUsers: List<Long> = emptyList(),
) : UIState {
    companion object {
        fun initial() = ChatState()
    }
}

sealed class ChatIntent : UIEvent {
    data class LoadInitial(val initialData: ChatLoadResponse): ChatIntent()
    class ClearAll(): ChatIntent()
    class Reset(): ChatIntent()
    data class AppendNewChats(
        val chats: List<ChatMessage>,
        val isHistory: Boolean,
    ): ChatIntent()
    data class SendChat(val message: String): ChatIntent()
    class ClearUnreadCount(): ChatIntent()
    class LoadMore(): ChatIntent()
    data class BrowseUser(val id: Long): ChatIntent()
    data class FollowUser(val id: Long): ChatIntent()
}

sealed class ChatEffect: UIEffect {
    class ScrollToLatest: ChatEffect()
    data class ShowResSnackbar(val textRes: StringResource): ChatEffect()
    data class ShowSnackbar(val text: String): ChatEffect()
    data class NavigateToScreen(val destination: Screen): ChatEffect()
    data class ControlProfileDialog(val visible: Boolean): ChatEffect()
}