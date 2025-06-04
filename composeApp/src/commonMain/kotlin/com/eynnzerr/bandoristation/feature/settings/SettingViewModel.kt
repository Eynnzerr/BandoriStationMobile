package com.eynnzerr.bandoristation.feature.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.viewModelScope
import com.eynnzerr.bandoristation.base.BaseViewModel
import com.eynnzerr.bandoristation.business.datastore.SetPreferenceUseCase
import com.eynnzerr.bandoristation.business.datastore.SetPreferenceUseCase.*
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingViewModel(
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
                    isRecordingRoomHistory = p[PreferenceKeys.RECORD_ROOM_HISTORY] ?: true
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
        }
    }
}