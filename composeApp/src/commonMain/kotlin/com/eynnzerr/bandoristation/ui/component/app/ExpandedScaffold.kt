package com.eynnzerr.bandoristation.ui.component.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import com.eynnzerr.bandoristation.navigation.Screen
import com.eynnzerr.bandoristation.ui.common.LocalAppProperty
import org.jetbrains.compose.resources.painterResource

@Composable
fun ExpandedScaffold(
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
    val appProperty = LocalAppProperty.current

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        AnimatedVisibility(
            visible = showNavigation,
        ) {
            AppNavRail(
                screens = screens,
                showBadges = showBadges,
                onNavigateTo = onNavigateTo,
                currentDestination = currentDestination,
                logo = {
                    appProperty.bandTheme.bandIcon?.let {
                        Image(
                            painter = painterResource(it),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp).padding(bottom = 16.dp)
                        )
                    }
                }
            )
        }

        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = topBar,
            bottomBar = bottomBar,
            snackbarHost = snackbarHost,
            floatingActionButton = floatingActionButton,
            floatingActionButtonPosition = floatingActionButtonPosition,
            content = content,
            containerColor = Color.Transparent
        )
    }
}