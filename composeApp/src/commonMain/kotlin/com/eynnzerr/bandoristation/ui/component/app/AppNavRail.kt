package com.eynnzerr.bandoristation.ui.component.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.eynnzerr.bandoristation.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun AppNavRail(
    screens: List<Screen>,
    showBadges: List<Boolean> = emptyList(),
    onNavigateTo: (Screen) -> Unit,
    currentDestination: NavDestination?,
    logo: @Composable () -> Unit = {},
) {
    val state = rememberWideNavigationRailState()
    val scope = rememberCoroutineScope()

    WideNavigationRail(
        state = state,
        header = {
            IconButton(
                modifier = Modifier.padding(start = 24.dp),
                onClick = {
                    scope.launch {
                        if (state.targetValue == WideNavigationRailValue.Expanded)
                            state.collapse()
                        else state.expand()
                    }
                },
            ) {
                if (state.targetValue == WideNavigationRailValue.Expanded) {
                    Icon(Icons.AutoMirrored.Filled.MenuOpen, "")
                } else {
                    Icon(Icons.Filled.Menu, "")
                }
            }
        },
    ) {
        screens.forEachIndexed { index, screen ->
            val selected: Boolean =
                currentDestination?.hierarchy?.any { it.route == screen.route } == true

            WideAppNavRailItem(
                expanded = state.targetValue == WideNavigationRailValue.Expanded,
                screen = screen,
                showBadge = showBadges.getOrElse(index) { false },
                selected = selected,
                onClick = onNavigateTo,
            )
        }
    }
}