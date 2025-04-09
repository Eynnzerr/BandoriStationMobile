package com.eynnzerr.bandoristation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.eynnzerr.bandoristation.di.appModule
import com.eynnzerr.bandoristation.navigation.RootNavGraph
import com.eynnzerr.bandoristation.navigation.Screen
import io.ktor.client.HttpClient
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication

@OptIn(ExperimentalCoilApi::class)
@Composable
@Preview
fun App() {
    KoinApplication(
        application = { modules(appModule) }
    ) {
        MaterialTheme {
            val appNavController = rememberNavController()
            RootNavGraph(
                navController = appNavController,
                startDestination = Screen.Home,
            )
        }
    }
}