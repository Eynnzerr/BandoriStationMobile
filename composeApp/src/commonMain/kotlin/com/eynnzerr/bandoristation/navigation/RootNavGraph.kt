package com.eynnzerr.bandoristation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.eynnzerr.bandoristation.feature.account.AccountScreen
import com.eynnzerr.bandoristation.feature.chat.ChatScreen
import com.eynnzerr.bandoristation.feature.home.HomeScreen
import com.eynnzerr.bandoristation.feature.settings.SettingScreen
import com.eynnzerr.bandoristation.feature.tutorial.TutorialScreen
import com.eynnzerr.bandoristation.handler.CheckUnreadChatHandler
import com.eynnzerr.bandoristation.navigation.ext.animatedComposable
import com.eynnzerr.bandoristation.navigation.ext.navigateTo
import com.eynnzerr.bandoristation.ui.common.LocalAppProperty
import com.eynnzerr.bandoristation.ui.component.app.SuiteScaffold
import org.koin.compose.koinInject

@Composable
fun RootNavGraph (
    navController: NavHostController,
    startDestination: Screen,
    checkUnreadChatHandler: CheckUnreadChatHandler = koinInject()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val isExpanded = LocalAppProperty.current.screenInfo.isLandscape()
    var showBadges by remember { mutableStateOf(listOf(false, false, false)) }

    LaunchedEffect(Unit) {
        checkUnreadChatHandler.unreadState.collect {
            if (it) {
                showBadges = showBadges.toMutableList().apply { set(1, true) }
            }
        }
    }

    LaunchedEffect(navBackStackEntry) {
        if (navBackStackEntry?.destination?.route == Destinations.CHAT_ROUTE) {
            showBadges = showBadges.toMutableList().apply { set(1, false) }
        }
    }

    SuiteScaffold(
        isExpanded = isExpanded,
        screens = Screen.bottomScreenList,
        showBadges = showBadges,
        showNavigation = navBackStackEntry?.destination?.route in Screen.bottomScreenList.map { it.route },
        currentDestination = navBackStackEntry?.destination,
        onNavigateTo = { navController.navigateTo(it) },
        bottomBar = {},
    ) { innerPadding ->
        NavHost(
            navController = navController,
            route = "host",
            startDestination = startDestination.route,
            modifier = Modifier
                .padding(bottom = innerPadding.calculateBottomPadding()),
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