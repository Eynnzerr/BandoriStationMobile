package com.eynnzerr.bandoristation.feature.chat

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.viewModelScope
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.clear_chats_snackbar
import bandoristationm.composeapp.generated.resources.load_all_snackbar
import com.eynnzerr.bandoristation.base.BaseViewModel
import com.eynnzerr.bandoristation.business.ConnectWebSocketUseCase
import com.eynnzerr.bandoristation.business.DisconnectWebSocketUseCase
import com.eynnzerr.bandoristation.business.GetChatUseCase
import com.eynnzerr.bandoristation.business.InitializeChatRoomUseCase
import com.eynnzerr.bandoristation.business.ReceiveHistoryChatUseCase
import com.eynnzerr.bandoristation.business.RequestHistoryChatUseCase
import com.eynnzerr.bandoristation.business.SendChatUseCase
import com.eynnzerr.bandoristation.business.SetAccessPermissionUseCase
import com.eynnzerr.bandoristation.business.SetUpClientUseCase
import com.eynnzerr.bandoristation.business.account.GetUserInfoUseCase
import com.eynnzerr.bandoristation.business.social.FollowUserUseCase
import com.eynnzerr.bandoristation.business.websocket.ReceiveNoticeUseCase
import com.eynnzerr.bandoristation.data.remote.websocket.WebSocketClient
import com.eynnzerr.bandoristation.feature.chat.ChatEffect.*
import com.eynnzerr.bandoristation.model.ChatMessage
import com.eynnzerr.bandoristation.model.ClientSetInfo
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.UserInfo
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import com.eynnzerr.bandoristation.utils.AppLogger
import com.eynnzerr.bandoristation.utils.formatTimestampAsDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class ChatViewModel(
    private val connectWebSocketUseCase: ConnectWebSocketUseCase,
    private val receiveNoticeUseCase: ReceiveNoticeUseCase,
    private val setAccessPermissionUseCase: SetAccessPermissionUseCase,
    private val setUpClientUseCase: SetUpClientUseCase,
    private val disconnectWebSocketUseCase: DisconnectWebSocketUseCase,
    private val initializeChatRoomUseCase: InitializeChatRoomUseCase,
    private val getChatUseCase: GetChatUseCase,
    private val sendChatUseCase: SendChatUseCase,
    private val requestHistoryChatUseCase: RequestHistoryChatUseCase,
    private val receiveHistoryChatUseCase: ReceiveHistoryChatUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val followUserUseCase: FollowUserUseCase,
    private val dataStore: DataStore<Preferences>,
) : BaseViewModel<ChatState, ChatIntent, ChatEffect>(
    initialState = ChatState.initial()
) {

    override suspend fun loadInitialData() {
        viewModelScope.launch {
            connectWebSocketUseCase(Unit).collect { result ->
                if (result is UseCaseResult.Success) {
                    when (result.data) {
                        is WebSocketClient.ConnectionState.Connected -> {
                            AppLogger.d(TAG, "WebSocket is connected.")

                            internalState.update {
                                it.copy(title = "聊天室")
                            }
                            setAccessPermissionUseCase(null)
                            setUpClientUseCase(ClientSetInfo(
                                client = "BandoriStation",
                                sendRoomNumber = false,
                                sendChat = true,
                            ))
                            initializeChatRoom()
                        }
                        is WebSocketClient.ConnectionState.Connecting -> {
                            internalState.update {
                                it.copy(title = "连接中...")
                            }
                            AppLogger.d(TAG, "Connecting to WebSocket...")
                        }
                        is WebSocketClient.ConnectionState.Disconnected -> {
                            // 出现Disconnected的情况：1) 断网 2) 进入后台超过30秒，服务器断开连接
                            internalState.update {
                                it.copy(title = "当前离线")
                            }
                            sendEffect(ShowSnackbar("正在重新连接至车站服务器..."))
                        }
                        is WebSocketClient.ConnectionState.Error -> {
                            internalState.update {
                                it.copy(title = "错误")
                            }
                            sendEffect(ShowSnackbar(result.data.exception.message ?: "WebSocket error."))
                        }
                    }
                }
            }
        }

        viewModelScope.launch {
            receiveNoticeUseCase.invoke(Unit).collect { result ->
                when (result) {
                    is UseCaseResult.Loading -> Unit
                    is UseCaseResult.Error -> Unit
                    is UseCaseResult.Success -> {
                        sendEffect(ShowSnackbar(result.data))
                    }
                }
            }
        }

        viewModelScope.launch {
            getChatUseCase.invoke(Unit).collect { result ->
                when (result) {
                    is UseCaseResult.Error -> Unit
                    is UseCaseResult.Loading -> Unit
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
                            sendEffect(ShowResSnackbar(Res.string.load_all_snackbar))

                        }
                    }
                }
            }
        }

        viewModelScope.launch {
            dataStore.data.collect { p ->
                internalState.update {
                    it.copy(
                        followingUsers = p[PreferenceKeys.FOLLOWING_LIST]?.map { s -> s.toLong() } ?: emptyList()
                    )
                }
            }
        }
    }

    override suspend fun onStartStateFlow() {
        // 每次重新进入页面，收集消息流前，都要重置消息列表，获取在其他页面停留期间服务端新增的消息
        initializeChatRoom()

        setUpClientUseCase(ClientSetInfo(
            client = "BandoriStation",
            sendRoomNumber = false,
            sendChat = true,
        ))
    }

    private suspend fun initializeChatRoom() {
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
                    title = "聊天室"
                ) to null
            }
            is ChatIntent.ClearAll -> {
                state.value.copy(
                    chats = emptyList(),
                    unreadCount = 0,
                    isLoading = false,
                ) to ShowResSnackbar(Res.string.clear_chats_snackbar)
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
                val earliestTimestamp = state.value.chats.firstOrNull()?.timestamp ?: Clock.System.now().toEpochMilliseconds()
                viewModelScope.launch(Dispatchers.IO) {
                    requestHistoryChatUseCase(earliestTimestamp)
                }
                state.value.copy(isLoading = true) to null
            }

            is ChatIntent.Reset -> {
                ChatState.initial() to null
            }

            is ChatIntent.BrowseUser -> {
                viewModelScope.launch {
                    val response = getUserInfoUseCase.invoke(event.id)
                    when (response) {
                        is UseCaseResult.Loading -> Unit
                        is UseCaseResult.Error -> {
                            sendEffect(ShowSnackbar(response.error))
                        }
                        is UseCaseResult.Success -> {
                            internalState.update {
                                it.copy(selectedUser = response.data)
                            }
                            sendEffect(ControlProfileDialog(true))
                        }
                    }
                }
                null to null
            }

            is ChatIntent.FollowUser -> {
                viewModelScope.launch {
                    val response = followUserUseCase.invoke(event.id)
                    when (response) {
                        is UseCaseResult.Loading -> Unit
                        is UseCaseResult.Error -> {
                            sendEffect(ShowSnackbar(response.error))
                        }
                        is UseCaseResult.Success -> {
                            sendEffect(ShowSnackbar(response.data))
                        }
                    }
                }
                null to null
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch { disconnectWebSocketUseCase(Unit) }
    }

    private fun processNewChat(newChats: List<ChatMessage>): List<ChatMessage> {
        val processedChatList = state.value.chats.toMutableList()
        for (chat in newChats) {
            val lastLatestTimestamp = if (processedChatList.isEmpty()) 0L else processedChatList.last().timestamp
            if (chat.timestamp - lastLatestTimestamp > 300000) {
                val timeMessage = ChatMessage(
                    timestamp = chat.timestamp,
                    content = formatTimestampAsDate(chat.timestamp),
                    userInfo = UserInfo(),
                )
                processedChatList.add(timeMessage)
            }
            processedChatList.add(chat)
        }
        return processedChatList
    }

    private fun processHistoryChat(historyChats: List<ChatMessage>): List<ChatMessage> {
        val processedChatList = mutableListOf<ChatMessage>()
        for (chat in historyChats) {
            val lastLatestTimestamp = if (processedChatList.isEmpty()) 0L else processedChatList.last().timestamp
            if (chat.timestamp - lastLatestTimestamp > 300000) {
                val timeMessage = ChatMessage(
                    timestamp = chat.timestamp,
                    content = formatTimestampAsDate(chat.timestamp),
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