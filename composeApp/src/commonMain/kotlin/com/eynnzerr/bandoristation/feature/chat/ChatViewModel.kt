package com.eynnzerr.bandoristation.feature.chat

import androidx.lifecycle.viewModelScope
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.clear_chats_snackbar
import bandoristationm.composeapp.generated.resources.load_all_snackbar
import com.eynnzerr.bandoristation.base.BaseViewModel
import com.eynnzerr.bandoristation.business.DisconnectWebSocketUseCase
import com.eynnzerr.bandoristation.business.GetChatUseCase
import com.eynnzerr.bandoristation.business.InitializeChatRoomUseCase
import com.eynnzerr.bandoristation.business.ReceiveHistoryChatUseCase
import com.eynnzerr.bandoristation.business.RequestHistoryChatUseCase
import com.eynnzerr.bandoristation.business.SendChatUseCase
import com.eynnzerr.bandoristation.business.SetUpClientUseCase
import com.eynnzerr.bandoristation.feature.chat.ChatEffect.*
import com.eynnzerr.bandoristation.model.ChatMessage
import com.eynnzerr.bandoristation.model.ClientSetInfo
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.UserInfo
import com.eynnzerr.bandoristation.utils.AppLogger
import com.eynnzerr.bandoristation.utils.formatTimestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlin.time.Clock.System
import kotlin.time.ExperimentalTime

class ChatViewModel(
    private val setUpClientUseCase: SetUpClientUseCase,
    private val disconnectWebSocketUseCase: DisconnectWebSocketUseCase,
    private val initializeChatRoomUseCase: InitializeChatRoomUseCase,
    private val getChatUseCase: GetChatUseCase,
    private val sendChatUseCase: SendChatUseCase,
    private val requestHistoryChatUseCase: RequestHistoryChatUseCase,
    private val receiveHistoryChatUseCase: ReceiveHistoryChatUseCase,
) : BaseViewModel<ChatState, ChatIntent, ChatEffect>(
    initialState = ChatState.initial()
) {

    override suspend fun loadInitialData() {
        viewModelScope.launch {
            getChatUseCase.invoke(Unit).collect { result ->
                when (result) {
                    is UseCaseResult.Error -> Unit
                    UseCaseResult.Loading -> Unit
                    is UseCaseResult.Success -> {
                        AppLogger.d(TAG, "Successfully received ${result.data.size} new chats")
                        val newChats = result.data
                        sendEvent(ChatIntent.AppendNewChats(newChats, isHistory = false))
                    }
                }
            }
        }

        viewModelScope.launch {
            receiveHistoryChatUseCase.invoke(Unit).collect { result ->
                when (result) {
                    is UseCaseResult.Error -> Unit
                    is UseCaseResult.Loading -> Unit
                    is UseCaseResult.Success -> {
                        val response = result.data
                        sendEvent(ChatIntent.AppendNewChats(response.messageList, isHistory = true))
                        if (response.isEnd) {
                            sendEffect(ShowSnackbar(Res.string.load_all_snackbar))

                        }
                    }
                }
            }
        }
    }

    override suspend fun onStartStateFlow() {
        setUpClientUseCase(ClientSetInfo(
            client = "BandoriStation Mobile",
            sendRoomNumber = false,
            sendChat = true,
        ))

        sendEvent(ChatIntent.Reset())

        when (val response = initializeChatRoomUseCase(Unit)) {
            is UseCaseResult.Error -> Unit
            is UseCaseResult.Loading -> Unit
            is UseCaseResult.Success -> {
                val initialResponse = response.data
                sendEvent(ChatIntent.LoadInitial(initialResponse))
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    override fun reduce(event: ChatIntent): Pair<ChatState?, ChatEffect?> {
        return when (event) {
            is ChatIntent.LoadInitial -> {
                val processedChatList = processNewChat(event.initialData.messageList)
                state.value.copy(
                    chats = processedChatList,
                    hasMore = !event.initialData.isEnd,
                    selfId = event.initialData.selfId,
                    initialized = true,
                    unreadCount = 0,
                    isLoading = false,
                ) to null
            }
            is ChatIntent.ClearAll -> {
                state.value.copy(
                    chats = emptyList(),
                    unreadCount = 0,
                    isLoading = false,
                ) to ShowSnackbar(Res.string.clear_chats_snackbar)
            }

            is ChatIntent.SendChat -> {
                viewModelScope.launch(Dispatchers.IO) {
                    sendChatUseCase(event.message)
                }
                null to null
            }

            is ChatIntent.AppendNewChats -> {
                val processedChatList = if (event.isHistory) processHistoryChat(event.chats) else processNewChat(event.chats)
                state.value.copy(
                    chats = processedChatList,
                    unreadCount = state.value.unreadCount + if (event.isHistory) 0 else event.chats.size,
                    isLoading = false,
                ) to null
            }

            is ChatIntent.ClearUnreadCount -> {
                state.value.copy(unreadCount = 0) to null
            }

            is ChatIntent.LoadMore -> {
                val earliestTimestamp = state.value.chats.firstOrNull()?.timestamp ?: System.now().toEpochMilliseconds()
                viewModelScope.launch(Dispatchers.IO) {
                    requestHistoryChatUseCase(earliestTimestamp)
                }
                state.value.copy(isLoading = true) to null
            }

            is ChatIntent.Reset -> {
                ChatState.initial() to null
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch { disconnectWebSocketUseCase(Unit) }
    }

    fun processNewChat(newChats: List<ChatMessage>): List<ChatMessage> {
        val processedChatList = state.value.chats.toMutableList()
        for (chat in newChats) {
            val lastLatestTimestamp = if (processedChatList.isEmpty()) 0L else processedChatList.last().timestamp
            if (chat.timestamp - lastLatestTimestamp > 300000) {
                val timeMessage = ChatMessage(
                    timestamp = chat.timestamp,
                    content = formatTimestamp(chat.timestamp),
                    userInfo = UserInfo(),
                )
                processedChatList.add(timeMessage)
            }
            processedChatList.add(chat)
        }
        return processedChatList
    }

    fun processHistoryChat(historyChats: List<ChatMessage>): List<ChatMessage> {
        val processedChatList = mutableListOf<ChatMessage>()
        for (chat in historyChats) {
            val lastLatestTimestamp = if (processedChatList.isEmpty()) 0L else processedChatList.last().timestamp
            if (chat.timestamp - lastLatestTimestamp > 300000) {
                val timeMessage = ChatMessage(
                    timestamp = chat.timestamp,
                    content = formatTimestamp(chat.timestamp),
                    userInfo = UserInfo(),
                )
                processedChatList.add(timeMessage)
            }
            processedChatList.add(chat)
        }
        return processedChatList + state.value.chats
    }
}

private const val TAG = "ChatViewModel"