package com.eynnzerr.bandoristation.ui.component.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.eynnzerr.bandoristation.navigation.Screen

@Composable
fun AppNavRail(
    screens: List<Screen>,
    showBadges: List<Boolean> = emptyList(),
    onNavigateTo: (Screen) -> Unit,
    currentDestination: NavDestination?,
    logo: @Composable () -> Unit = {},
) {
    NavigationRail(
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        containerColor = Color.Transparent,
        modifier = Modifier.padding(vertical = 14.dp)
    ) {
        logo()

        screens.forEachIndexed { index, screen ->
            val selected: Boolean =
                currentDestination?.hierarchy?.any { it.route == screen.route } == true

            AppNavRailItem(
                screen = screen,
                showBadge = showBadges.getOrElse(index) { false },
                selected = selected,
                onClick = onNavigateTo
            )
        }
    }
}