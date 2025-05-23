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
import com.eynnzerr.bandoristation.business.SetUpClientUseCase
import com.eynnzerr.bandoristation.business.UpdateTimestampUseCase
import com.eynnzerr.bandoristation.business.UploadRoomUseCase
import com.eynnzerr.bandoristation.business.datastore.GetPreferenceUseCase
import com.eynnzerr.bandoristation.business.datastore.SetPreferenceUseCase
import com.eynnzerr.bandoristation.business.datastore.SetPreferenceUseCase.*
import com.eynnzerr.bandoristation.feature.home.HomeEffect.*
import com.eynnzerr.bandoristation.model.ClientSetInfo
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import com.eynnzerr.bandoristation.utils.AppLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock.System

class HomeViewModel(
    private val connectWebSocketUseCase: ConnectWebSocketUseCase,
    private val setUpClientUseCase: SetUpClientUseCase,
    private val disconnectWebSocketUseCase: DisconnectWebSocketUseCase,
    private val getRoomListUseCase: GetRoomListUseCase,
    private val updateTimestampUseCase: UpdateTimestampUseCase,
    private val checkUnreadChatUseCase: CheckUnreadChatUseCase,
    private val uploadRoomUseCase: UploadRoomUseCase,
    private val setPreferenceUseCase: SetPreferenceUseCase,
    private val stringSetPreferenceUseCase: GetPreferenceUseCase<Set<String>>,
) : BaseViewModel<HomeState, HomeIntent, HomeEffect>(
    initialState = HomeState.initial()
) {

    override suspend fun loadInitialData() {
        AppLogger.d(TAG, "loadInitialData: Initialize HomeViewModel!")

        connectWebSocketUseCase(Unit)

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
    }

    override suspend fun onStartStateFlow() {
        setUpClientUseCase(ClientSetInfo(
            client = "BandoriStation Mobile",
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
                state.value.copy(rooms = event.rooms) to null
            }

            is HomeIntent.AppendRoomList -> {
                val originalList = state.value.rooms
                state.value.copy(rooms = originalList + event.rooms) to null
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
                ) to event.room?.let { ShowSnackbar(Res.string.join_room_snackbar) }
            }

            is HomeIntent.UploadRoom -> {
                viewModelScope.launch(Dispatchers.IO) {
                    uploadRoomUseCase(event.room)
                }
                null to ShowSnackbar(Res.string.upload_room_snackbar)
            }

            is HomeIntent.AddPresetWord -> {
                viewModelScope.launch(Dispatchers.IO) {
                    setPreferenceUseCase(
                        Params(
                            key = PreferenceKeys.PRESET_WORDS,
                            value = state.value.presetWords.toMutableSet().apply { add(event.word) }
                        ))
                }
                null to null
            }
            is HomeIntent.RemovePresetWord -> {
                viewModelScope.launch(Dispatchers.IO) {
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
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch { disconnectWebSocketUseCase(Unit) }
    }
}

private const val TAG = "HomeViewModel"