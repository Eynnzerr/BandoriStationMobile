package com.eynnzerr.bandoristation.feature.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.viewModelScope
import com.eynnzerr.bandoristation.base.BaseViewModel
import com.eynnzerr.bandoristation.feature.settings.SettingEffect.*
import com.eynnzerr.bandoristation.usecase.SetUpClientUseCase
import com.eynnzerr.bandoristation.model.ClientSetInfo
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.model.account.AccountInfo
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import com.eynnzerr.bandoristation.usecase.account.GetUserInfoUseCase
import com.eynnzerr.bandoristation.usecase.clientName
import com.eynnzerr.bandoristation.usecase.encryption.GetBlackWhiteListUseCase
import com.eynnzerr.bandoristation.usecase.encryption.RegisterEncryptionUseCase
import com.eynnzerr.bandoristation.usecase.encryption.RemoveFromBlacklistUseCase
import com.eynnzerr.bandoristation.usecase.encryption.RemoveFromWhitelistUseCase
import com.eynnzerr.bandoristation.usecase.encryption.UpdateInviteCodeUseCase
import com.eynnzerr.bandoristation.usecase.social.FollowUserUseCase
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

class SettingViewModel(
    private val setUpClientUseCase: SetUpClientUseCase,
    private val registerEncryptionUseCase: RegisterEncryptionUseCase,
    private val updateInviteCodeUseCase: UpdateInviteCodeUseCase,
    private val removeFromBlacklistUseCase: RemoveFromBlacklistUseCase,
    private val removeFromWhitelistUseCase: RemoveFromWhitelistUseCase,
    private val getBlackWhiteListUseCase: GetBlackWhiteListUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val followUserUseCase: FollowUserUseCase,
    private val dataStore: DataStore<Preferences>,
) : BaseViewModel<SettingState, SettingEvent, SettingEffect>(
    initialState = SettingState.initial()
) {
    override suspend fun onInitialize() {
        dataStore.data.collect { p ->
            val currentTimestamp = Clock.System.now().toEpochMilliseconds()
            val registerExpireTimestamp = p[PreferenceKeys.REGISTER_EXPIRE_TIME] ?: currentTimestamp
            val encryptionValidDays = (registerExpireTimestamp - currentTimestamp).milliseconds.inWholeDays

            internalState.update {
                it.copy(
                    themeName = p[PreferenceKeys.BAND_THEME] ?: "",
                    isFilteringPJSK = p[PreferenceKeys.FILTER_PJSK] ?: true,
                    isClearingOutdatedRoom = p[PreferenceKeys.CLEAR_OUTDATED_ROOM] ?: false,
                    isShowingPlayerInfo = p[PreferenceKeys.SHOW_PLAER_BRIEF] ?: false,
                    isRecordingRoomHistory = p[PreferenceKeys.RECORD_ROOM_HISTORY] ?: true,
                    autoUploadInterval = p[PreferenceKeys.AUTO_UPLOAD_INTERVAL] ?: 10L,
                    isEncryptionEnabled = p[PreferenceKeys.ENCRYPTION_TOKEN] != null && encryptionValidDays > 0,
                    encryptionValidDays = encryptionValidDays,
                    inviteCode = p[PreferenceKeys.ENCRYPTION_INVITE_CODE] ?: "",
                    followingUsers = p[PreferenceKeys.FOLLOWING_LIST]?.map { s -> s.toLong() } ?: emptyList()
                )
            }
        }
    }

    override fun reduce(event: SettingEvent): Pair<SettingState?, SettingEffect?> {
        return when (event) {
            is SettingEvent.UpdateBandTheme -> {
                viewModelScope.launch {
                    dataStore.edit { p -> p[PreferenceKeys.BAND_THEME] = event.name }
                }
                null to null
            }

            is SettingEvent.UpdateFilterPJSK -> {
                viewModelScope.launch {
                    dataStore.edit { p -> p[PreferenceKeys.FILTER_PJSK] = event.isFiltering }
                }
                null to null
            }

            is SettingEvent.UpdateClearOutdatedRoom -> {
                viewModelScope.launch {
                    dataStore.edit { p -> p[PreferenceKeys.CLEAR_OUTDATED_ROOM] = event.isClearing }
                }
                null to null
            }

            is SettingEvent.UpdateShowPlayerInfo -> {
                viewModelScope.launch {
                    dataStore.edit { p -> p[PreferenceKeys.SHOW_PLAER_BRIEF] = event.isShowing }
                }
                null to null
            }

            is SettingEvent.UpdateRecordRoomHistory -> {
                viewModelScope.launch {
                    dataStore.edit { p -> p[PreferenceKeys.RECORD_ROOM_HISTORY] = event.isRecording }
                }
                null to null
            }

            is SettingEvent.UpdateAutoUploadInterval -> {
                viewModelScope.launch {
                    dataStore.edit { p -> p[PreferenceKeys.AUTO_UPLOAD_INTERVAL] = event.interval }
                }
                null to null
            }

            is SettingEvent.UpdateEnableEncryption -> {
                viewModelScope.launch {
                    dataStore.edit { p -> p[PreferenceKeys.ENABLE_ENCRYPTION] = event.enabled }
                }
                null to null
            }

            is SettingEvent.RegisterEncryption -> {
                viewModelScope.launch {
                    when (val result = registerEncryptionUseCase.invoke(Unit)) {
                        is UseCaseResult.Success -> {
                            sendEffect(ShowSnackbar("注册/刷新加密服务成功。"))
                        }
                        is UseCaseResult.Error -> {
                            sendEffect(ShowSnackbar(result.error))
                        }
                        is UseCaseResult.Loading -> Unit
                    }
                }
                null to null
            }

            is SettingEvent.UpdateInviteCode -> {
                viewModelScope.launch {
                    dataStore.edit { p -> p[PreferenceKeys.ENCRYPTION_INVITE_CODE] = event.code }
                    when (val result = updateInviteCodeUseCase.invoke(event.code)) {
                        is UseCaseResult.Success -> {
                            sendEffect(ShowSnackbar("邀请码更新成功。"))
                        }
                        is UseCaseResult.Error -> {
                            sendEffect(ShowSnackbar(result.error))
                        }
                        is UseCaseResult.Loading -> Unit
                    }
                }
                null to null
            }

            is SettingEvent.RemoveFromBlackList -> {
                viewModelScope.launch {
                    when (val result = removeFromBlacklistUseCase.invoke(event.id)) {
                        is UseCaseResult.Success -> {
                            sendEffect(ShowSnackbar("已将用户ID ${event.id}从黑名单移除。"))
                            val removedList = state.value.blacklist.toMutableList().apply {
                                remove(event.id)
                            }
                            internalState.update {
                                it.copy(
                                    blacklist = removedList
                                )
                            }
                        }
                        is UseCaseResult.Error -> {
                            sendEffect(ShowSnackbar(result.error))
                        }
                        is UseCaseResult.Loading -> Unit
                    }
                }
                null to null
            }

            is SettingEvent.RemoveFromWhiteList -> {
                viewModelScope.launch {
                    when (val result = removeFromWhitelistUseCase.invoke(event.id)) {
                        is UseCaseResult.Success -> {
                            sendEffect(ShowSnackbar("已将用户ID ${event.id}从白名单移除。"))
                            val removedList = state.value.whitelist.toMutableList().apply {
                                remove(event.id)
                            }
                            internalState.update {
                                it.copy(
                                    whitelist = removedList
                                )
                            }
                        }
                        is UseCaseResult.Error -> {
                            sendEffect(ShowSnackbar(result.error))
                        }
                        is UseCaseResult.Loading -> Unit
                    }
                }
                null to null
            }

            is SettingEvent.FetchWhiteBlackList -> {
                viewModelScope.launch {
                    when (val result = getBlackWhiteListUseCase.invoke(Unit)) {
                        is UseCaseResult.Success -> {
                            internalState.update {
                                it.copy(
                                    blacklist = result.data.blacklist,
                                    whitelist = result.data.whitelist,
                                )
                            }
                        }
                        is UseCaseResult.Error -> {
                            sendEffect(ShowSnackbar(result.error))
                        }
                        is UseCaseResult.Loading -> Unit
                    }
                }
                null to null
            }

            is SettingEvent.BrowseUser -> {
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
                        }
                    }
                }
                state.value.copy(selectedUser = AccountInfo()) to ControlProfileDialog(true)
            }

            is SettingEvent.FollowUser -> {
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
        }
    }
}