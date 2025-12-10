package com.eynnzerr.bandoristation.ui.component.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import com.eynnzerr.bandoristation.navigation.Screen

@Composable
fun CompactScaffold(
    modifier: Modifier = Modifier,
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
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = topBar,
        bottomBar = {
            AnimatedVisibility(visible = showNavigation) {
                AppNavBar(
                    screens = screens,
                    showBadges = showBadges,
                    currentDestination = currentDestination,
                    onNavigateTo = onNavigateTo,
                )
            }
        },
        snackbarHost = snackbarHost,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        content = content
    )
}