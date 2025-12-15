package com.eynnzerr.bandoristation.ui.component.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun SettingDropdownItem(
    modifier: Modifier = Modifier,
    enable: Boolean = true,
    title: String,
    desc: String? = null,
    icon: ImageVector? = null,
    content: @Composable () -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        SettingItem(
            title = title,
            modifier = modifier,
            enable = enable,
            desc = desc,
            icon = icon,
            action = {
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = ""
                )
            },
            onClick = { expanded = !expanded }
        )

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            content()
        }
    }
}