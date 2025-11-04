package com.eynnzerr.bandoristation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.account_screen_title
import bandoristationm.composeapp.generated.resources.chat_screen_title
import bandoristationm.composeapp.generated.resources.home_screen_title
import bandoristationm.composeapp.generated.resources.settings_screen_title
import bandoristationm.composeapp.generated.resources.tutorial_screen_title
import org.jetbrains.compose.resources.StringResource

sealed class Screen(
    val route: String,
    var routePath: String? = null,
    var clearBackStack: Boolean = false,
    val restoreState: Boolean = true,
    val title: StringResource? = null,
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null
) {
    fun withClearBackStack() = apply { clearBackStack = true }

    fun routeWith(path: String) = apply {
        routePath = path
    }

    object Home: Screen(
        route = Destinations.HOME_ROUTE,
        title = Res.string.home_screen_title,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
    )

    object Chat: Screen(
        route = Destinations.CHAT_ROUTE,
        title = Res.string.chat_screen_title,
        selectedIcon = Icons.Filled.Forum,
        unselectedIcon = Icons.Outlined.Forum,
    )

    object Account: Screen(
        route = Destinations.ACCOUNT_ROUTE,
        title = Res.string.account_screen_title,
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
    )

    object Settings: Screen(
        route = Destinations.SETTINGS_ROUTE,
        title = Res.string.settings_screen_title,
    )

    object Tutorial: Screen(
        route = Destinations.TUTORIAL_ROUTE,
        title = Res.string.tutorial_screen_title,
    )

    companion object {
        val bottomScreenList by lazy {
            listOf(
                Home,
                Chat,
                Account,
            )
        }
    }
}