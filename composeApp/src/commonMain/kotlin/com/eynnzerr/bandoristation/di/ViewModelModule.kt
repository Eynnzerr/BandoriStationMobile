package com.eynnzerr.bandoristation.di

import com.eynnzerr.bandoristation.feature.chat.ChatViewModel
import com.eynnzerr.bandoristation.feature.home.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun provideViewModelModule() = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::ChatViewModel)
}