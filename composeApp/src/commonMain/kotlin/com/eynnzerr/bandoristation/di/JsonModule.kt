package com.eynnzerr.bandoristation.di

import kotlinx.serialization.json.Json
import org.koin.dsl.module

fun provideJsonModule() = module {
    single {
        Json {
            classDiscriminator = "class_type" // 类判别器命名
            isLenient = true  // 宽松模式
            ignoreUnknownKeys = true  // 忽略多余键值对
            coerceInputValues = true  // 强制类型转换
        }
    }
}