package com.eynnzerr.bandoristation.ui.component.app

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.placeholder
import com.eynnzerr.bandoristation.navigation.Screen
import org.jetbrains.compose.resources.stringResource

@Composable
fun ColumnScope.AppNavRailItem(
    screen: Screen,
    showBadge: Boolean = false,
    selected: Boolean,
    onClick: (Screen) -> Unit,
) {
    @Composable
    fun ItemIcon() {
        Icon(
            imageVector = if (selected) screen.selectedIcon ?: Icons.Default.Warning else screen.unselectedIcon ?: Icons.Default.Warning,
            contentDescription = null,
        )
    }

    NavigationRailItem(
        selected = selected,
        onClick = { onClick(screen) },
        icon = {
            if (showBadge) {
                BadgedBox(
                    badge = {
                        Badge(
                            modifier =
                                Modifier.semantics { contentDescription = "New chats" }
                        )
                    }
                ) {
                    ItemIcon()
                }
            } else {
                ItemIcon()
            }
        },
        label = { Text(text = stringResource(screen.title ?: Res.string.placeholder)) },
        enabled = true,
        alwaysShowLabel = true,
        colors = NavigationRailItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            indicatorColor =  MaterialTheme.colorScheme.primaryContainer
        )
    )
}