package com.eynnzerr.bandoristation.feature.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.viewModelScope
import com.eynnzerr.bandoristation.base.BaseViewModel
import com.eynnzerr.bandoristation.usecase.SetUpClientUseCase
import com.eynnzerr.bandoristation.model.ClientSetInfo
import com.eynnzerr.bandoristation.model.UseCaseResult
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import com.eynnzerr.bandoristation.usecase.clientName
import com.eynnzerr.bandoristation.usecase.encryption.RegisterEncryptionUseCase
import com.eynnzerr.bandoristation.usecase.encryption.UpdateInviteCodeUseCase
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingViewModel(
    private val setUpClientUseCase: SetUpClientUseCase,
    private val registerEncryptionUseCase: RegisterEncryptionUseCase,
    private val updateInviteCodeUseCase: UpdateInviteCodeUseCase,
    private val dataStore: DataStore<Preferences>,
) : BaseViewModel<SettingState, SettingEvent, SettingEffect>(
    initialState = SettingState.initial()
) {
    override suspend fun onInitialize() {
        dataStore.data.collect { p ->
            internalState.update {
                it.copy(
                    themeName = p[PreferenceKeys.BAND_THEME] ?: "",
                    isFilteringPJSK = p[PreferenceKeys.FILTER_PJSK] ?: true,
                    isClearingOutdatedRoom = p[PreferenceKeys.CLEAR_OUTDATED_ROOM] ?: false,
                    isShowingPlayerInfo = p[PreferenceKeys.SHOW_PLAER_BRIEF] ?: false,
                    isRecordingRoomHistory = p[PreferenceKeys.RECORD_ROOM_HISTORY] ?: true,
                    autoUploadInterval = p[PreferenceKeys.AUTO_UPLOAD_INTERVAL] ?: 10L,
                    isEncryptionEnabled = p[PreferenceKeys.ENABLE_ENCRYPTION] ?: false,
                    inviteCode = p[PreferenceKeys.ENCRYPTION_INVITE_CODE] ?: ""
                )
            }
        }
    }

    override suspend fun onStartStateFlow() {
        setUpClientUseCase(ClientSetInfo(
            client = clientName,
            sendRoomNumber = false,
            sendChat = false,
        ))
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
                    val result = registerEncryptionUseCase.invoke(Unit)
                    when (result) {
                        is UseCaseResult.Success -> {
                            sendEffect(SettingEffect.ShowSnackbar("注册/刷新加密服务成功。"))
                        }
                        is UseCaseResult.Error -> {
                            sendEffect(SettingEffect.ShowSnackbar(result.error))
                        }
                        is UseCaseResult.Loading -> Unit
                    }
                }
                null to null
            }

            is SettingEvent.UpdateInviteCode -> {
                viewModelScope.launch {
                    dataStore.edit { p -> p[PreferenceKeys.ENCRYPTION_INVITE_CODE] = event.code }
                    val result = updateInviteCodeUseCase.invoke(event.code)
                    when (result) {
                        is UseCaseResult.Success -> {
                            sendEffect(SettingEffect.ShowSnackbar("邀请码更新成功。"))
                        }
                        is UseCaseResult.Error -> {
                            sendEffect(SettingEffect.ShowSnackbar(result.error))
                        }
                        is UseCaseResult.Loading -> Unit
                    }
                }
                null to null
            }
        }
    }
}