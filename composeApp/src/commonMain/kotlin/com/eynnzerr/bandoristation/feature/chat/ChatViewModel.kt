package com.eynnzerr.bandoristation.feature.chat

import androidx.lifecycle.viewModelScope
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.clear_chats_snackbar
import com.eynnzerr.bandoristation.base.BaseViewModel
import com.eynnzerr.bandoristation.business.DisconnectWebSocketUseCase
import com.eynnzerr.bandoristation.business.GetChatUseCase
import com.eynnzerr.bandoristation.business.InitializeChatRoomUseCase
import com.eynnzerr.bandoristation.business.SendChatUseCase
import com.eynnzerr.bandoristation.business.SetUpClientUseCase
import com.eynnzerr.bandoristation.feature.chat.ChatEffect.*
import com.eynnzerr.bandoristation.model.ClientSetInfo
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.utils.AppLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

class ChatViewModel(
    private val setUpClientUseCase: SetUpClientUseCase,
    private val disconnectWebSocketUseCase: DisconnectWebSocketUseCase,
    private val initializeChatRoomUseCase: InitializeChatRoomUseCase,
    private val getChatUseCase: GetChatUseCase,
    private val sendChatUseCase: SendChatUseCase,
) : BaseViewModel<ChatState, ChatIntent, ChatEffect>(
    initialState = ChatState.initial()
) {

    override suspend fun loadInitialData() {
        when (val response = initializeChatRoomUseCase(Unit)) {
            is UseCaseResult.Error -> Unit
            is UseCaseResult.Loading -> Unit
            is UseCaseResult.Success -> {
                val initialResponse = response.data
                sendEvent(ChatIntent.LoadInitial(initialResponse))
            }
        }

        viewModelScope.launch {
            getChatUseCase.invoke(Unit).collect { result ->
                when (result) {
                    is UseCaseResult.Error -> Unit
                    UseCaseResult.Loading -> Unit
                    is UseCaseResult.Success -> {
                        AppLogger.d(TAG, "Successfully received ${result.data.size} new chats")
                        val newChats = result.data
                        sendEvent(ChatIntent.AppendNewChats(newChats))
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
    }

    override fun reduce(event: ChatIntent): Pair<ChatState?, ChatEffect?> {
        return when (event) {
            is ChatIntent.LoadInitial -> {
                state.value.copy(
                    chats = event.initialData.messageList,
                    hasMore = !event.initialData.isEnd,
                    selfId = event.initialData.selfId,
                    initialized = true,
                    unreadCount = 20,
                ) to null
            }
            is ChatIntent.LoadMore -> {
                null to null
            }
            is ChatIntent.ClearAll -> {
                state.value.copy(
                    chats = emptyList(),
                    unreadCount = 0,
                ) to ShowSnackbar(Res.string.clear_chats_snackbar)
            }

            is ChatIntent.SendChat -> {
                viewModelScope.launch(Dispatchers.IO) {
                    sendChatUseCase(event.message)
                }
                null to null
            }

            is ChatIntent.AppendNewChats -> {
                state.value.copy(
                    chats = state.value.chats + event.chats,
                    unreadCount = state.value.unreadCount + 1
                ) to null
            }

            is ChatIntent.ClearUnreadCount -> {
                state.value.copy(unreadCount = 0) to null
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch { disconnectWebSocketUseCase(Unit) }
    }
}

private const val TAG = "ChatViewModel"