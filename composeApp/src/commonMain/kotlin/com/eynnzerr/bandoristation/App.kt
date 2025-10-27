package com.eynnzerr.bandoristation

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.navigation.compose.rememberNavController
import coil3.annotation.ExperimentalCoilApi
import com.eynnzerr.bandoristation.di.appModule
import com.eynnzerr.bandoristation.navigation.RootNavGraph
import com.eynnzerr.bandoristation.navigation.Screen
import com.eynnzerr.bandoristation.preferences.PreferenceKeys
import com.eynnzerr.bandoristation.preferences.PreferencesManager
import com.eynnzerr.bandoristation.ui.common.AppProperty
import com.eynnzerr.bandoristation.ui.common.LocalAppProperty
import com.eynnzerr.bandoristation.ui.theme.BandThemeConfig
import com.eynnzerr.bandoristation.ui.theme.BandoriTheme
import com.eynnzerr.bandoristation.ui.theme.getBandConfig
import com.eynnzerr.bandoristation.utils.ScreenInfo
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication

@OptIn(ExperimentalCoilApi::class)
@Composable
@Preview
fun App() {
    KoinApplication(
        application = { modules(appModule) }
    ) {
        WebSocketLifecycleHandler()

        val preferences by PreferencesManager.preferencesFlow().collectAsState(initial = null)
        var screenInfo by remember { mutableStateOf(ScreenInfo()) }

        val themeConfig = preferences?.let { p ->
            getBandConfig(p[PreferenceKeys.BAND_THEME])
        } ?: BandThemeConfig.Default

        val isFirstLaunch = preferences?.let { p ->
            p[PreferenceKeys.IS_FIRST_LAUNCH] ?: true
        }

        val appProperty = AppProperty(
            screenInfo = screenInfo,
            bandTheme = themeConfig
        )

        BandoriTheme(seedColor = Color(themeConfig.seedColorLong)) {
            Layout(
                content = {
                    val appNavController = rememberNavController()
                    CompositionLocalProvider(LocalAppProperty provides appProperty) {
                        if (isFirstLaunch != null) {
                            RootNavGraph(
                                navController = appNavController,
                                startDestination = if (isFirstLaunch) Screen.Tutorial else Screen.Home,
                            )
                        }
                        // While loading, this will be empty, preventing a flicker.
                    }
                },
                measurePolicy = { measurables, constraints ->
                    val width = constraints.maxWidth // px
                    val height = constraints.maxHeight // px
                    screenInfo = ScreenInfo(width.toDp() to height.toDp())

                    val placeables = measurables.map { measurable ->
                        measurable.measure(constraints)
                    }
                    layout(width, height) {
                        var yPosition = 0
                        placeables.forEach { placeable ->
                            placeable.placeRelative(x = 0, y = yPosition)
                            yPosition += placeable.height
                        }
                    }
                }
            )
        }
    }
}
