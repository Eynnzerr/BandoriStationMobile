package com.eynnzerr.bandoristation.di

import com.eynnzerr.bandoristation.feature.account.AccountViewModel
import com.eynnzerr.bandoristation.feature.chat.ChatViewModel
import com.eynnzerr.bandoristation.feature.home.HomeViewModel
import com.eynnzerr.bandoristation.feature.settings.SettingViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun provideViewModelModule() = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::ChatViewModel)
    viewModelOf(::AccountViewModel)
    viewModelOf(::SettingViewModel)
}