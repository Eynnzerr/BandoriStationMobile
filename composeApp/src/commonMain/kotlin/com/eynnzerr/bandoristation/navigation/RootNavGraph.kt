package com.eynnzerr.bandoristation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.eynnzerr.bandoristation.feature.account.AccountScreen
import com.eynnzerr.bandoristation.feature.chat.ChatScreen
import com.eynnzerr.bandoristation.feature.home.HomeScreen
import com.eynnzerr.bandoristation.feature.settings.SettingScreen
import com.eynnzerr.bandoristation.feature.tutorial.TutorialScreen
import com.eynnzerr.bandoristation.navigation.ext.animatedComposable
import com.eynnzerr.bandoristation.navigation.ext.navigateTo
import com.eynnzerr.bandoristation.ui.common.LocalAppProperty
import com.eynnzerr.bandoristation.ui.component.app.SuiteScaffold

@Composable
fun RootNavGraph (
    navController: NavHostController,
    startDestination: Screen,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val isExpanded = LocalAppProperty.current.screenInfo.isLandscape()

    SuiteScaffold(
        isExpanded = isExpanded,
        screens = Screen.bottomScreenList,
        showBadges = listOf(false, false, false), // TODO
        showNavigation = navBackStackEntry?.destination?.route in Screen.bottomScreenList.map { it.route },
        currentDestination = navBackStackEntry?.destination,
        onNavigateTo = { navController.navigateTo(it) },
        bottomBar = {},
    ) { innerPadding ->
        NavHost(
            navController = navController,
            route = "host",
            startDestination = startDestination.route,
            modifier = Modifier.padding(innerPadding), // 包括bottomBar的padding
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
}