package com.eynnzerr.bandoristation.feature.home

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.viewModelScope
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.connecting
import bandoristationm.composeapp.generated.resources.error
import bandoristationm.composeapp.generated.resources.inform_user_success
import bandoristationm.composeapp.generated.resources.join_room_snackbar
import bandoristationm.composeapp.generated.resources.offline
import bandoristationm.composeapp.generated.resources.reconnecting_to_server
import bandoristationm.composeapp.generated.resources.refreshing_room_list
import bandoristationm.composeapp.generated.resources.room_list_title
import bandoristationm.composeapp.generated.resources.set_filter_success
import bandoristationm.composeapp.generated.resources.upload_room_snackbar
import bandoristationm.composeapp.generated.resources.websocket_error_default
import com.eynnzerr.bandoristation.base.BaseViewModel
import com.eynnzerr.bandoristation.ui.dialog.RequestRoomState
import com.eynnzerr.bandoristation.usecase.chat.CheckUnreadChatUseCase
import com.eynnzerr.bandoristation.usecase.websocket.GetWebSocketStateUseCase
import com.eynnzerr.bandoristation.usecase.room.GetRoomListUseCase
import com.eynnzerr.bandoristation.usecase.SetAccessPermissionUseCase
import com.eynnzerr.bandoristation.usecase.SetUpClientUseCase
import com.eynnzerr.bandoristation.usecase.time.UpdateTimestampUseCase
import com.eynnzerr.bandoristation.usecase.room.UploadRoomUseCase
import com.eynnzerr.bandoristation.usecase.room.GetRoomFilterUseCase
import com.eynnzerr.bandoristation.usecase.room.RequestRecentRoomsUseCase
import com.eynnzerr.bandoristation.usecase.room.UpdateRoomFilterUseCase
import com.eynnzerr.bandoristation.usecase.roomhistory.AddRoomHistoryUseCase
import com.eynnzerr.bandoristation.usecase.social.InformUserUseCase
import com.eynnzerr.bandoristation.usecase.websocket.ReceiveNoticeUseCase
import com.eynnzerr.bandoristation.usecase.GetLatestReleaseUseCase
import com.eynnzerr.bandoristation.usecase.account.GetUserInfoUseCase
import com.eynnzerr.bandoristation.usecase.social.FollowUserUseCase
import com.eynnzerr.bandoristation.usecase.time.GetServerTimeUseCase
import com.eynnzerr.bandoristation.data.remote.websocket.WebSocketClient
import com.eynnzerr.bandoristation.feature.home.HomeEffect.*
import com.eynnzerr.bandoristation.model.ClientSetInfo
import com.eynnzerr.bandoristation.model.room.RoomFilter
import com.eynnzerr.bandoristation.model.room.RoomHistory
import com.eynnzerr.bandoristation.model.room.RoomInfo
import com.eynnzerr.bandoristation.model.SourceInfo
import com.eynnzerr.bandoristation.model.UserInfo
import com.eynnzerr.bandoristation.getPlatform
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.room.RoomAccessRequest
import com.eynnzerr.bandoristation.model.room.RoomAccessResponse
import com.eynnzerr.bandoristation.model.room.RoomUploadInfo
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import com.eynnzerr.bandoristation.usecase.clientName
import com.eynnzerr.bandoristation.usecase.encryption.EncryptionAggregator
import com.eynnzerr.bandoristation.utils.AppLogger
import com.eynnzerr.bandoristation.utils.generateUUID
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.isActive
import kotlin.time.Clock

class HomeViewModel(
    private val getWebSocketStateUseCase: GetWebSocketStateUseCase,
    private val receiveNoticeUseCase: ReceiveNoticeUseCase,
    private val getServerTimeUseCase: GetServerTimeUseCase,
    private val setAccessPermissionUseCase: SetAccessPermissionUseCase,
    private val setUpClientUseCase: SetUpClientUseCase,
    private val requestRecentRoomsUseCase: RequestRecentRoomsUseCase,
    private val getRoomListUseCase: GetRoomListUseCase,
    private val updateTimestampUseCase: UpdateTimestampUseCase,
    private val checkUnreadChatUseCase: CheckUnreadChatUseCase,
    private val uploadRoomUseCase: UploadRoomUseCase,
    private val informUserUseCase: InformUserUseCase,
    private val getRoomFilterUseCase: GetRoomFilterUseCase,
    private val updateRoomFilterUseCase: UpdateRoomFilterUseCase,
    private val addRoomHistoryUseCase: AddRoomHistoryUseCase,
    private val getLatestReleaseUseCase: GetLatestReleaseUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val followUserUseCase: FollowUserUseCase,
    private val encryptionAggregator: EncryptionAggregator,
    private val dataStore: DataStore<Preferences>,
) : BaseViewModel<HomeState, HomeIntent, HomeEffect>(
    initialState = HomeState.initial()
) {
    var isFilteringPJSK = true
    var isClearingOutdatedRoom = false
    var isSavingRoomHistory = true
    var autoUploadInterval: Long = 5L
    private var autoUploadJob: Job? = null
    private var waitingRequestId: String? = null // 当在对话框点击申请后，随即记录当前requestId。即这个字段始终跟踪最后在对话框中申请的id
    private val pendingRequestMap = mutableMapOf<String, RoomInfo>() // 当前正在等待批准的全部申请 requestId to roomInfo

    override suspend fun onInitialize() {
        sendEvent(HomeIntent.GetRoomFilter())

        viewModelScope.launch {
            getWebSocketStateUseCase(Unit).collect { result ->
                if (result is UseCaseResult.Success) {
                    when (result.data) {
                        is WebSocketClient.ConnectionState.Connected -> {
                            AppLogger.d(TAG, "WebSocket is connected.")

                            // 每次重新连接到websocket时，请求最近房间列表、重新设置权限、设置客户端
                            internalState.update {
                                it.copy(title = Res.string.room_list_title)
                            }
                            requestRecentRoomsUseCase(Unit)
                            setAccessPermissionUseCase(null)
                            setUpClientUseCase(ClientSetInfo(
                                client = clientName,
                                sendRoomNumber = true,
                                sendChat = false,
                            ))
                        }
                        is WebSocketClient.ConnectionState.Connecting -> {
                            internalState.update {
                                it.copy(title = Res.string.connecting)
                            }
                            AppLogger.d(TAG, "Connecting to WebSocket...")
                        }
                        is WebSocketClient.ConnectionState.Disconnected -> {
                            // 出现Disconnected的情况：1) 断网 2) 进入后台超过30秒，服务器断开连接 3) WebSocketClient 报错
                            internalState.update {
                                it.copy(title = Res.string.offline)
                            }
                            sendEffect(ShowResourceSnackbar(Res.string.reconnecting_to_server))
                        }
                        is WebSocketClient.ConnectionState.Error -> {
                            internalState.update {
                                it.copy(title = Res.string.error)
                            }
                            sendEffect(ShowSnackbar(result.data.exception.message ?: Res.string.websocket_error_default.key))
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
                        if (isClearingOutdatedRoom) {
                            sendEvent(HomeIntent.UpdateRoomList(state.value.rooms.filter {
                                result.data - (it.time ?: 0) <= 60000L
                            }))
                        }
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

                        // correct server timestamp if local server time falls behind latest server room time for 1.5s and more.
                        result.data.lastOrNull()?.let { lastRoom ->
                            if ((lastRoom.time ?: Clock.System.now().toEpochMilliseconds()) - state.value.serverTimestampMillis > 1500L) {
                                getServerTimeUseCase(Unit)
                            }
                        }
                    }
                }
            }
        }

        viewModelScope.launch {
            dataStore.edit { p -> p[PreferenceKeys.IS_FIRST_LAUNCH] = false }
            dataStore.data.collect { p ->
                isFilteringPJSK = p[PreferenceKeys.FILTER_PJSK] ?: true
                isClearingOutdatedRoom = p[PreferenceKeys.CLEAR_OUTDATED_ROOM] ?: false
                isSavingRoomHistory = p[PreferenceKeys.RECORD_ROOM_HISTORY] ?: true
                autoUploadInterval = p[PreferenceKeys.AUTO_UPLOAD_INTERVAL] ?: 5L

                internalState.update {
                    it.copy(
                        presetWords = p[PreferenceKeys.PRESET_WORDS] ?: emptySet(),
                        followingUsers = p[PreferenceKeys.FOLLOWING_LIST]?.map { s -> s.toLong() } ?: emptyList(),
                        userId = p[PreferenceKeys.USER_ID] ?: 0,
                    )
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

        viewModelScope.launch {
            encryptionAggregator.listenRoomAccessRequest().collect { result ->
                when (result) {
                    is UseCaseResult.Loading -> Unit
                    is UseCaseResult.Error -> {
                        AppLogger.d(TAG, "Failed to receive encrypted room access request.")
                    }
                    is UseCaseResult.Success -> {
                        val request = result.data
                        internalState.update {
                            it.copy(accessRequestQueue = it.accessRequestQueue + request)
                        }
                    }
                }
            }
        }

        viewModelScope.launch {
            encryptionAggregator.listenRoomAccessResponse().collect { result ->
                when (result) {
                    is UseCaseResult.Loading -> Unit
                    is UseCaseResult.Error -> {
                        AppLogger.d(TAG, "Failed to receive encrypted room access response.")
                    }
                    is UseCaseResult.Success -> {
                        val response = result.data
                        if (state.value.showRequestRoomDialog && response.requestId == waitingRequestId) {
                            // 若当前用户正在对话框界面等待结果，直接在对话框上显示
                            if (response.approved) {
                                internalState.update {
                                    it.copy(
                                        requestRoomState = RequestRoomState.SUCCESS,
                                        decryptedRoomNumber = response.roomNumber ?: ""
                                    )
                                }
                            } else {
                                internalState.update {
                                    it.copy(
                                        requestRoomState = RequestRoomState.ERROR,
                                        requestRoomError = response.message ?: ""
                                    )
                                }
                            }

                            waitingRequestId = null
                        } else {
                            // 当前用户已退出对话框 or 进入新申请的对话框。通过带复制按钮的snackbar显示
                            pendingRequestMap[response.requestId]?.let { roomInfo ->
                                if (response.approved) {
                                    val text = "你申请查看的用户${roomInfo.userInfo?.username}的实际车牌为: ${response.roomNumber} (${roomInfo.rawMessage})"
                                    sendEffect(ShowRequestResultBySnackbar(text, response.roomNumber?: ""))
                                } else {
                                    val text = "用户${roomInfo.userInfo?.username}拒绝了你的申请，原因: ${response.message}"
                                    sendEffect(ShowSnackbar(text))
                                }
                            }
                        }

                        pendingRequestMap.remove(response.requestId)
                    }
                }
            }
        }

        viewModelScope.launch {
            when (val result = getLatestReleaseUseCase(Unit)) {
                is UseCaseResult.Success -> {
                    val latest = result.data.tagName.trimStart('v', 'V')
                    val current = getPlatform().versionName.trimStart('v', 'V')

                    val latestParts = latest.split(".").map { it.toIntOrNull() ?: 0 }
                    val currentParts = current.split(".").map { it.toIntOrNull() ?: 0 }

                    var shouldUpdate = false
                    for (i in 0 until maxOf(latestParts.size, currentParts.size)) {
                        val latestPart = latestParts.getOrElse(i) { 0 }
                        val currentPart = currentParts.getOrElse(i) { 0 }
                        if (latestPart > currentPart) {
                            shouldUpdate = true
                            break
                        }
                        if (latestPart < currentPart) {
                            break
                        }
                    }

                    if (shouldUpdate) {
                        sendEffect(OpenUpdateDialog(result.data))
                    }
                }
                else -> Unit
            }
        }
    }

    override suspend fun onStartStateFlow() {
        // 每次重新进入房间页，设置客户端接收条件
        setUpClientUseCase(ClientSetInfo(
            client = clientName,
            sendRoomNumber = true,
            sendChat = false,
        ))

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
                state.value.copy(rooms = filteredRooms) to null
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
                state.value.copy(serverTimestampMillis = event.timestampMillis) to null
            }

            is HomeIntent.JoinRoom -> {
                val selectedRoom = event.room
                val previousRoom = state.value.selectedRoom
                val currentTimestamp = state.value.serverTimestampMillis

                if (isSavingRoomHistory && previousRoom != selectedRoom && previousRoom != null) {
                    viewModelScope.launch {
                        addRoomHistoryUseCase.invoke(RoomHistory(
                            number = previousRoom.number ?: "",
                            rawMessage = previousRoom.rawMessage ?: "",
                            sourceInfo = previousRoom.sourceInfo ?: SourceInfo(),
                            type = previousRoom.type ?: "",
                            time = previousRoom.time ?: 0,
                            userInfo = previousRoom.userInfo ?: UserInfo(),
                            loginId = 0,
                            duration = currentTimestamp - state.value.joinedTimestampMillis,
                        ))
                    }
                }

                state.value.copy(
                    selectedRoom = event.room,
                    joinedTimestampMillis = currentTimestamp,
                ) to event.room?.let { ShowResourceSnackbar(Res.string.join_room_snackbar) }
            }

            is HomeIntent.UploadRoom -> {
                autoUploadJob?.cancel()

                val uploadInfo = RoomUploadInfo(
                    number = if (event.encrypted) "999999" else event.number,
                    description = event.description
                )

                if (event.continuous) {
                    autoUploadJob = viewModelScope.launch {
                        while (isActive) {
                            uploadRoomUseCase(uploadInfo)
                            delay(autoUploadInterval * 1000L)
                        }
                    }
                } else {
                    viewModelScope.launch {
                        uploadRoomUseCase(uploadInfo)
                    }
                }

                if (event.encrypted) {
                    viewModelScope.launch {
                        encryptionAggregator.uploadEncryptedRoom(event.number)
                    }
                }

                val newState = state.value.selectedRoom?.let {
                    state.value.copy(selectedRoom = it.copy(number = event.number, rawMessage = event.description))
                } ?: state.value

                val finalState = if (event.encrypted) {
                    newState.copy(encryptedRoomNumber = event.number)
                } else {
                    newState
                }

                finalState to ShowResourceSnackbar(Res.string.upload_room_snackbar)
            }

            is HomeIntent.AddPresetWord -> {
                viewModelScope.launch {
                    dataStore.edit { p ->
                        p[PreferenceKeys.PRESET_WORDS] = state.value.presetWords.toMutableSet().apply { add(event.word) }
                    }
                }
                null to null
            }
            is HomeIntent.RemovePresetWord -> {
                viewModelScope.launch {
                    dataStore.edit { p ->
                        p[PreferenceKeys.PRESET_WORDS] = state.value.presetWords.toMutableSet().apply { remove(event.word) }
                    }
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
                    when (val informResult = informUserUseCase.invoke(event.params)) {
                        is UseCaseResult.Loading -> Unit
                        is UseCaseResult.Error -> {
                            sendEffect(ShowSnackbar(informResult.error))
                        }
                        is UseCaseResult.Success -> {
                            sendEffect(ShowResourceSnackbar(Res.string.inform_user_success))
                        }
                    }
                }
                null to CloseInformUserDialog()
            }

            is HomeIntent.GetRoomFilter -> {
                viewModelScope.launch {
                    when (val getFilterResult = getRoomFilterUseCase.invoke(Unit)) {
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
                    when (val updateFilterResult = updateRoomFilterUseCase.invoke(event.filter)) {
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
                            sendEffect(ShowResourceSnackbar(Res.string.set_filter_success))
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
                null to ShowResourceSnackbar(Res.string.refreshing_room_list)
            }

            is HomeIntent.SetNoReminder -> {
                viewModelScope.launch {
                    dataStore.edit { p ->
                        p[PreferenceKeys.IS_FIRST_LAUNCH] = false
                    }
                }
                null to null
            }

            is HomeIntent.BrowseUser -> {
                viewModelScope.launch {
                    when (val response = getUserInfoUseCase.invoke(event.id)) {
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

            is HomeIntent.FollowUser -> {
                viewModelScope.launch {
                    when (val response = followUserUseCase.invoke(event.id)) {
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

            is HomeIntent.OnRequestRoom -> {
                state.value.copy(
                    showRequestRoomDialog = true,
                    requestingRoomInfo = event.room,
                    requestRoomState = RequestRoomState.INITIAL,
                    decryptedRoomNumber = null,
                    requestRoomError = null
                ) to null
            }

            is HomeIntent.OnDismissRequestRoomDialog -> {
                waitingRequestId = null // clear waiting flag.
                state.value.copy(showRequestRoomDialog = false) to null
            }

            is HomeIntent.OnSubmitInviteCode -> {
                viewModelScope.launch {
                    val request = ApiRequest.VerifyInviteCodeRequest(
                        targetUserId = event.targetUser,
                        inviteCode = event.code,
                    )
                    when (val result = encryptionAggregator.verifyInviteCode(request)) {
                        is UseCaseResult.Success -> {
                            internalState.update {
                                it.copy(
                                    requestRoomState = RequestRoomState.SUCCESS,
                                    decryptedRoomNumber = result.data
                                )
                            }
                        }
                        is UseCaseResult.Error -> {
                            internalState.update {
                                it.copy(
                                    requestRoomState = RequestRoomState.ERROR,
                                    requestRoomError = result.error
                                )
                            }
                        }
                        is UseCaseResult.Loading -> Unit
                    }
                }

                null to null
            }

            is HomeIntent.OnApplyOnline -> {
                internalState.update { it.copy(requestRoomState = RequestRoomState.PENDING_APPROVAL) }
                event.requestingRoomInfo?.let { roomInfo ->
                    viewModelScope.launch {
                        val requestId = generateUUID()
                        val request = dataStore.data.map { p ->
                            RoomAccessRequest(
                                requestId = requestId,
                                targetUserId = roomInfo.userInfo?.userId?.toString() ?: "",
                                requesterId = (p[PreferenceKeys.USER_ID] ?: 0).toString(),
                                requesterName = p[PreferenceKeys.USER_NAME] ?: "",
                                requesterAvatar = p[PreferenceKeys.USER_AVATAR] ?: "",
                            )
                        }.first()
                        encryptionAggregator.requestEncryptedRoomAccess(request)

                        // record pending request states.
                        waitingRequestId = requestId
                        pendingRequestMap[requestId] = roomInfo
                    }
                }

                null to null
            }

            is HomeIntent.RespondToAccessRequest -> {
                val currentRequest = state.value.accessRequestQueue.firstOrNull()
                if (currentRequest != null) {
                    viewModelScope.launch {
                        // 1. Respond to the user
                        encryptionAggregator.respondToRoomAccess(RoomAccessResponse(
                            requestId = currentRequest.requestId,
                            approved = event.isApproved,
                            roomNumber = if (event.isApproved) state.value.encryptedRoomNumber else null,
                            message = if (event.isApproved) "欢迎上车" else "房主拒绝了你的请求"
                        ))

                        // 2. Handle blacklist/whitelist
                        if (event.addToWhiteList) {
                            encryptionAggregator.addToWhiteList(currentRequest.requesterId)
                        } else if (event.addToBlacklist) {
                            encryptionAggregator.addToBlacklist(currentRequest.requesterId)
                        }

                        // 3. Remove from queue
                        internalState.update {
                            it.copy(accessRequestQueue = it.accessRequestQueue.drop(1))
                        }
                    }
                }
                null to null
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        autoUploadJob?.cancel()
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