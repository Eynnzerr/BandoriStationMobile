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

/**
 * 由于设置页主要都是与preferences打交道，故仅限SettingViewModel，省略了UseCase层，直接依赖dataStore
 */
class SettingViewModel(
    private val dataStore: DataStore<Preferences>,
) : BaseViewModel<SettingState, SettingEvent, SettingEffect>(
    initialState = SettingState.initial()
) {
    override suspend fun loadInitialData() {
        dataStore.data.collect { p ->
            internalState.update {
                it.copy(
                    themeName = p[PreferenceKeys.BAND_THEME] ?: "",
                    isFilteringPJSK = p[PreferenceKeys.FILTER_PJSK] ?: true,
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
        }
    }
}