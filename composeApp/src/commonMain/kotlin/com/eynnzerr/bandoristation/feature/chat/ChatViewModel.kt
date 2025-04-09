package com.eynnzerr.bandoristation.feature.chat

import androidx.lifecycle.viewModelScope
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.clear_chats_snackbar
import com.eynnzerr.bandoristation.base.BaseViewModel
import com.eynnzerr.bandoristation.business.DisconnectWebSocketUseCase
import com.eynnzerr.bandoristation.business.InitializeChatRoomUseCase
import com.eynnzerr.bandoristation.business.SetUpClientUseCase
import com.eynnzerr.bandoristation.feature.chat.ChatEffect.*
import com.eynnzerr.bandoristation.model.ClientSetInfo
import com.eynnzerr.bandoristation.model.UseCaseResult
import kotlinx.coroutines.launch

class ChatViewModel(
    private val setUpClientUseCase: SetUpClientUseCase,
    private val disconnectWebSocketUseCase: DisconnectWebSocketUseCase,
    private val initializeChatRoomUseCase: InitializeChatRoomUseCase,
) : BaseViewModel<ChatState, ChatIntent, ChatEffect>(
    initialState = ChatState.initial()
) {

    override suspend fun loadInitialData() {
        setUpClientUseCase(ClientSetInfo(
            client = "BandoriStation Mobile",
            sendRoomNumber = false,
            sendChat = true,
        ))

        when (val response = initializeChatRoomUseCase(Unit)) {
            is UseCaseResult.Error -> Unit
            is UseCaseResult.Loading -> Unit
            is UseCaseResult.Success -> {
                val initialResponse = response.data
                sendEvent(ChatIntent.LoadInitial(initialResponse))
            }
        }
    }

    override fun reduce(event: ChatIntent): Pair<ChatState, ChatEffect?> {
        return when (event) {
            is ChatIntent.LoadInitial -> {
                state.value.copy(
                    chats = event.initialData.messageList,
                    hasMore = !event.initialData.isEnd,
                    selfId = event.initialData.selfId
                ) to ScrollToLatest()
            }
            is ChatIntent.LoadMore -> {
                state.value to null
            }
            is ChatIntent.ClearAll -> {
                state.value.copy(
                    chats = emptyList()
                ) to ShowSnackbar(Res.string.clear_chats_snackbar)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch { disconnectWebSocketUseCase(Unit) }
    }
}