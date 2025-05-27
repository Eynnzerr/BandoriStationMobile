package com.eynnzerr.bandoristation.feature.home

import androidx.lifecycle.viewModelScope
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.join_room_snackbar
import bandoristationm.composeapp.generated.resources.upload_room_snackbar
import com.eynnzerr.bandoristation.base.BaseViewModel
import com.eynnzerr.bandoristation.business.CheckUnreadChatUseCase
import com.eynnzerr.bandoristation.business.ConnectWebSocketUseCase
import com.eynnzerr.bandoristation.business.DisconnectWebSocketUseCase
import com.eynnzerr.bandoristation.business.GetRoomListUseCase
import com.eynnzerr.bandoristation.business.SetAccessPermissionUseCase
import com.eynnzerr.bandoristation.business.SetUpClientUseCase
import com.eynnzerr.bandoristation.business.UpdateTimestampUseCase
import com.eynnzerr.bandoristation.business.UploadRoomUseCase
import com.eynnzerr.bandoristation.business.datastore.GetPreferenceUseCase
import com.eynnzerr.bandoristation.business.datastore.SetPreferenceUseCase
import com.eynnzerr.bandoristation.business.datastore.SetPreferenceUseCase.*
import com.eynnzerr.bandoristation.business.room.GetRoomFilterUseCase
import com.eynnzerr.bandoristation.business.room.RequestRecentRoomsUseCase
import com.eynnzerr.bandoristation.business.room.UpdateRoomFilterUseCase
import com.eynnzerr.bandoristation.business.social.InformUserUseCase
import com.eynnzerr.bandoristation.business.websocket.ReceiveNoticeUseCase
import com.eynnzerr.bandoristation.data.remote.websocket.WebSocketClient
import com.eynnzerr.bandoristation.feature.home.HomeEffect.*
import com.eynnzerr.bandoristation.model.ClientSetInfo
import com.eynnzerr.bandoristation.model.RoomFilter
import com.eynnzerr.bandoristation.model.RoomInfo
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import com.eynnzerr.bandoristation.utils.AppLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock.System

class HomeViewModel(
    private val connectWebSocketUseCase: ConnectWebSocketUseCase,
    private val receiveNoticeUseCase: ReceiveNoticeUseCase,
    private val setAccessPermissionUseCase: SetAccessPermissionUseCase,
    private val setUpClientUseCase: SetUpClientUseCase,
    private val disconnectWebSocketUseCase: DisconnectWebSocketUseCase,
    private val requestRecentRoomsUseCase: RequestRecentRoomsUseCase,
    private val getRoomListUseCase: GetRoomListUseCase,
    private val updateTimestampUseCase: UpdateTimestampUseCase,
    private val checkUnreadChatUseCase: CheckUnreadChatUseCase,
    private val uploadRoomUseCase: UploadRoomUseCase,
    private val setPreferenceUseCase: SetPreferenceUseCase,
    private val stringSetPreferenceUseCase: GetPreferenceUseCase<Set<String>>,
    private val boolPreferenceUseCase: GetPreferenceUseCase<Boolean>,
    private val informUserUseCase: InformUserUseCase,
    private val getRoomFilterUseCase: GetRoomFilterUseCase,
    private val updateRoomFilterUseCase: UpdateRoomFilterUseCase,
) : BaseViewModel<HomeState, HomeIntent, HomeEffect>(
    initialState = HomeState.initial()
) {
    var isFilteringPJSK = true

    override suspend fun loadInitialData() {
        sendEvent(HomeIntent.GetRoomFilter())

        viewModelScope.launch {
            connectWebSocketUseCase(Unit).collect { result ->
                if (result is UseCaseResult.Success) {
                    when (result.data) {
                        is WebSocketClient.ConnectionState.Connected -> {
                            AppLogger.d(TAG, "WebSocket is connected.")

                            // 每次重新连接到websocket时，请求最近房间列表、重新设置权限、设置客户端
                            internalState.update {
                                it.copy(title = "房间列表")
                            }
                            requestRecentRoomsUseCase(Unit)
                            setAccessPermissionUseCase(null)
                            setUpClientUseCase(ClientSetInfo(
                                client = "BandoriStation",
                                sendRoomNumber = true,
                                sendChat = false,
                            ))
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
            updateTimestampUseCase.invoke(Unit).collect { result ->
                when (result) {
                    is UseCaseResult.Loading -> Unit
                    is UseCaseResult.Error -> Unit
                    is UseCaseResult.Success -> {
                        sendEvent(HomeIntent.UpdateTimestamp(result.data))
                    }
                }
            }
        }

        viewModelScope.launch {
            getRoomListUseCase.invoke(Unit).collect { result ->
                when (result) {
                    is UseCaseResult.Loading -> Unit
                    is UseCaseResult.Error -> {
                        AppLogger.d(TAG, "Failed to fetch room list.")
                    }
                    is UseCaseResult.Success -> {
                        AppLogger.d(TAG, "Successfully fetched room list. length: ${result.data.size}")
                        sendEvent(HomeIntent.AppendRoomList(result.data))
                    }
                }
            }
        }

        viewModelScope.launch {
            stringSetPreferenceUseCase.invoke(GetPreferenceUseCase.Params(PreferenceKeys.PRESET_WORDS, emptySet()))
                .collect { result ->
                    when (result) {
                        is UseCaseResult.Loading -> Unit
                        is UseCaseResult.Error -> {
                            AppLogger.d(TAG, result.error.message ?: "")
                        }
                        is UseCaseResult.Success -> {
                            sendEvent(HomeIntent.UpdatePresetWords(result.data ?: emptySet()))
                        }
                    }
                }
        }

        viewModelScope.launch {
            boolPreferenceUseCase.invoke(GetPreferenceUseCase.Params(PreferenceKeys.FILTER_PJSK, true))
                .collect { result ->
                    when (result) {
                        is UseCaseResult.Loading -> Unit
                        is UseCaseResult.Error -> {
                            AppLogger.d(TAG, result.error.message ?: "")
                        }
                        is UseCaseResult.Success -> {
                            isFilteringPJSK = result.data ?: true
                        }
                    }
                }
        }

        viewModelScope.launch {
            receiveNoticeUseCase.invoke(Unit).collect { result ->
                when (result) {
                    is UseCaseResult.Loading -> Unit
                    is UseCaseResult.Error -> {
                        AppLogger.d(TAG, "Failed to receive notice.")
                    }
                    is UseCaseResult.Success -> {
                        sendEffect(ShowSnackbar(result.data))
                    }
                }
            }
        }
    }

    override suspend fun onStartStateFlow() {
        // 每次重新进入房间页，设置客户端接收条件
        setUpClientUseCase(ClientSetInfo(
            client = "BandoriStation",
            sendRoomNumber = true,
            sendChat = false,
        ))

        // TODO BUG: 与设置访问权限请求异步。必须尽快解决不能批量发送ws action的问题，即改造泛型为密封类
        delay(2000L)
        when (val checkResult = checkUnreadChatUseCase(Unit)) {
            is UseCaseResult.Error -> Unit
            is UseCaseResult.Loading -> Unit
            is UseCaseResult.Success -> {
                sendEvent(HomeIntent.UpdateMessageBadge(checkResult.data))
            }
        }
    }

    override fun reduce(event: HomeIntent): Pair<HomeState?, HomeEffect?> {
        return when (event) {
            is HomeIntent.UpdateRoomList -> {
                val filteredRooms = event.rooms.filterNot(state.value.roomFilter, isFilteringPJSK)
                state.value.copy(rooms = filteredRooms) to ShowSnackbar("获取最新房间列表。")
            }

            is HomeIntent.AppendRoomList -> {
                val originalList = state.value.rooms
                val filteredRooms = event.rooms.filterNot(state.value.roomFilter, isFilteringPJSK)
                state.value.copy(rooms = originalList + filteredRooms) to null
            }

            is HomeIntent.UpdateMessageBadge -> {
                state.value.copy(hasUnReadMessages = event.hasUnReadMessages) to null
            }

            is HomeIntent.UpdateTimestamp -> {
                state.value.copy(localTimestampMillis = event.timestampMillis) to null
            }

            is HomeIntent.JoinRoom -> {
                state.value.copy(
                    selectedRoom = event.room,
                    joinedTimestampMillis = System.now().toEpochMilliseconds(),
                ) to event.room?.let { ShowResourceSnackbar(Res.string.join_room_snackbar) }
            }

            is HomeIntent.UploadRoom -> {
                viewModelScope.launch {
                    uploadRoomUseCase(event.room)
                }
                null to ShowResourceSnackbar(Res.string.upload_room_snackbar)
            }

            is HomeIntent.AddPresetWord -> {
                viewModelScope.launch {
                    setPreferenceUseCase(
                        Params(
                            key = PreferenceKeys.PRESET_WORDS,
                            value = state.value.presetWords.toMutableSet().apply { add(event.word) }
                        ))
                }
                null to null
            }
            is HomeIntent.RemovePresetWord -> {
                viewModelScope.launch {
                    setPreferenceUseCase(
                        Params(
                            key = PreferenceKeys.PRESET_WORDS,
                            value = state.value.presetWords.toMutableSet().apply { remove(event.word) }
                        ))
                }
                null to null
            }
            is HomeIntent.UpdatePresetWords -> {
                state.value.copy(presetWords = event.words) to null
            }

            is HomeIntent.ClearRooms -> {
                state.value.copy(
                    rooms = emptyList()
                ) to null
            }

            is HomeIntent.InformUser -> {
                viewModelScope.launch {
                    val informResult = informUserUseCase.invoke(event.params)
                    when (informResult) {
                        is UseCaseResult.Loading -> Unit
                        is UseCaseResult.Error -> {
                            sendEffect(ShowSnackbar(informResult.error))
                        }
                        is UseCaseResult.Success -> {
                            sendEffect(ShowSnackbar("成功举报该用户。"))
                        }
                    }
                }
                null to CloseInformUserDialog()
            }

            is HomeIntent.GetRoomFilter -> {
                viewModelScope.launch {
                    val getFilterResult = getRoomFilterUseCase.invoke(Unit)
                    when (getFilterResult) {
                        is UseCaseResult.Loading -> Unit
                        is UseCaseResult.Error -> {
                            sendEffect(ShowSnackbar(getFilterResult.error))
                        }
                        is UseCaseResult.Success -> {
                            internalState.update {
                                it.copy(
                                    roomFilter = getFilterResult.data
                                )
                            }
                        }
                    }
                }
                null to null
            }

            is HomeIntent.UpdateRoomFilter -> {
                viewModelScope.launch {
                    val updateFilterResult = updateRoomFilterUseCase.invoke(event.filter)
                    when (updateFilterResult) {
                        is UseCaseResult.Loading -> Unit
                        is UseCaseResult.Error -> {
                            sendEffect(ShowSnackbar(updateFilterResult.error))
                        }
                        is UseCaseResult.Success -> {
                            internalState.update {
                                it.copy(
                                    roomFilter = event.filter
                                )
                            }
                            sendEffect(ShowSnackbar("设置过滤条件成功。"))
                        }
                    }
                }
                null to null
            }

            is HomeIntent.RefreshRooms -> {
                viewModelScope.launch {
                    internalState.update {
                        it.copy(
                            rooms = emptyList(),
                        )
                    }

                    delay(500L)
                    requestRecentRoomsUseCase(Unit)
                }
                null to ShowSnackbar("刷新房间列表...")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch { disconnectWebSocketUseCase(Unit) }
    }

    fun List<RoomInfo>.filterNot(filter: RoomFilter, isFilteringPJSK: Boolean = true): List<RoomInfo> {
        return this.filter { roomInfo ->
            // 条件1: 不过滤掉type包含在filter.type中的元素
            val notMatchType = roomInfo.type == null || !filter.type.contains(roomInfo.type)

            // 条件2: 不过滤掉rawMessage能与filter.keyword中任一元素正则匹配的元素
            val notMatchKeyword = roomInfo.rawMessage == null || filter.keyword.none { keyword ->
                roomInfo.rawMessage.contains(Regex(keyword))
            }

            // 条件3: 不过滤掉userInfo.userId与filter.user中任一元素的userId相同的元素
            val notMatchUserId = roomInfo.userInfo?.userId == null ||
                    filter.user.none { it.userId != null && it.userId == roomInfo.userInfo.userId }

            // 条件4: 当isFilteringOther为true时，不过滤掉number少于6位数的元素
            val notMatchNumberLength = !isFilteringPJSK ||
                    roomInfo.number == null ||
                    roomInfo.number.length >= 6

            // 所有条件都满足才保留该元素
            notMatchType && notMatchKeyword && notMatchUserId && notMatchNumberLength
        }
    }
}

private const val TAG = "HomeViewModel"