package com.eynnzerr.bandoristation.ui.component.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.eynnzerr.bandoristation.navigation.Screen

@Composable
fun AppNavBar(
    screens: List<Screen>,
    showBadges: List<Boolean> = emptyList(),
    onNavigateTo: (Screen) -> Unit,
    currentDestination: NavDestination?
) {
    NavigationBar(
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
    ) {
        screens.forEachIndexed { index, screen ->
            val selected: Boolean =
                currentDestination?.hierarchy?.any { it.route == screen.route } == true

            AppNavBarItem(
                screen = screen,
                showBadge = showBadges.getOrElse(index) { false },
                selected = selected,
                onClick = onNavigateTo
            )
        }
    }
}