package com.eynnzerr.bandoristation.feature.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.viewModelScope
import com.eynnzerr.bandoristation.base.BaseViewModel
import com.eynnzerr.bandoristation.business.datastore.SetPreferenceUseCase
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import kotlinx.coroutines.launch

/**
 * 由于设置页主要都是与preferences打交道，故仅限SettingViewModel，省略了UseCase层，直接依赖dataStore
 */
class SettingViewModel(
    private val setPreferenceUseCase: SetPreferenceUseCase,
) : BaseViewModel<SettingState, SettingEvent, SettingEffect>(
    initialState = SettingState.initial()
) {
    override suspend fun loadInitialData() {

    }

    override fun reduce(event: SettingEvent): Pair<SettingState?, SettingEffect?> {
        return when (event) {
            is SettingEvent.UpdateBandTheme -> {
                viewModelScope.launch {
                    setPreferenceUseCase.invoke(
                        SetPreferenceUseCase.Params(
                            key = PreferenceKeys.BAND_THEME,
                            value = event.name
                        )
                    )
                }
                null to null
            }
        }
    }
}