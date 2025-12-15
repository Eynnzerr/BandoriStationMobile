package com.eynnzerr.bandoristation.ui.component.app

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.FabPosition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import com.eynnzerr.bandoristation.navigation.Screen

@Composable
fun SuiteScaffold(
    scaffoldModifier: Modifier = Modifier,
    isExpanded: Boolean,
    screens: List<Screen> = emptyList(),
    showBadges: List<Boolean> = emptyList(),
    showNavigation: Boolean = false,
    onNavigateTo: (Screen) -> Unit = {},
    currentDestination: NavDestination?,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    content: @Composable (PaddingValues) -> Unit
) {
    if (isExpanded) {
        ExpandedScaffold(
            modifier = scaffoldModifier,
            screens = screens,
            showBadges = showBadges,
            showNavigation = showNavigation,
            onNavigateTo = onNavigateTo,
            currentDestination = currentDestination,
            topBar = topBar,
            bottomBar = bottomBar,
            snackbarHost = snackbarHost,
            floatingActionButton = floatingActionButton,
            floatingActionButtonPosition = floatingActionButtonPosition,
            content = content
        )
    } else {
        CompactScaffold(
            modifier = scaffoldModifier,
            screens = screens,
            showBadges = showBadges,
            showNavigation = showNavigation,
            onNavigateTo = onNavigateTo,
            currentDestination = currentDestination,
            topBar = topBar,
            bottomBar = bottomBar,
            snackbarHost = snackbarHost,
            floatingActionButton = floatingActionButton,
            floatingActionButtonPosition = floatingActionButtonPosition,
            content = content
        )
    }
}