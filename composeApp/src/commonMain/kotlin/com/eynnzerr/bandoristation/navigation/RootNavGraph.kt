package com.eynnzerr.bandoristation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.eynnzerr.bandoristation.feature.account.AccountScreen
import com.eynnzerr.bandoristation.feature.chat.ChatScreen
import com.eynnzerr.bandoristation.feature.home.HomeScreen
import com.eynnzerr.bandoristation.feature.settings.SettingScreen
import com.eynnzerr.bandoristation.feature.tutorial.TutorialScreen
import com.eynnzerr.bandoristation.navigation.ext.animatedComposable

@Composable
fun RootNavGraph (
    navController: NavHostController,
    startDestination: Screen,
) {
    NavHost(
        navController = navController,
        route = "host",
        startDestination = startDestination.route,
    ) {
        animatedComposable(Destinations.HOME_ROUTE) {
            HomeScreen(navController)
        }
        animatedComposable(Destinations.CHAT_ROUTE) {
            ChatScreen(navController)
        }
        animatedComposable(Destinations.ACCOUNT_ROUTE) {
            AccountScreen(navController)
        }
        animatedComposable(Destinations.SETTINGS_ROUTE) {
            SettingScreen(navController)
        }
        animatedComposable(Destinations.TUTORIAL_ROUTE) {
            TutorialScreen(navController)
        }
    }
}