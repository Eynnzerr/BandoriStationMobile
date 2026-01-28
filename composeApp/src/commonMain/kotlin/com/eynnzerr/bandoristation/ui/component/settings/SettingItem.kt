package com.eynnzerr.bandoristation.ui.component.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.settings_encrypt_code_desc
import bandoristationm.composeapp.generated.resources.settings_encrypt_code_title
import bandoristationm.composeapp.generated.resources.settings_encrypt_list_desc
import bandoristationm.composeapp.generated.resources.settings_encrypt_list_title
import bandoristationm.composeapp.generated.resources.settings_tutorial_desc
import bandoristationm.composeapp.generated.resources.settings_tutorial_title
import bandoristationm.composeapp.generated.resources.settings_version_title
import com.eynnzerr.bandoristation.feature.settings.SettingEffect
import com.eynnzerr.bandoristation.ui.theme.BandoriTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingItem(
    modifier: Modifier = Modifier,
    enable: Boolean,
    selected: Boolean,
    title: String,
    desc: String? = null,
    icon: ImageVector? = null,
    action: (@Composable () -> Unit)? = null,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier
            .clickable(enabled = enable, onClick = onClick)
            .alpha(if (enable) 1f else 0.5f),
        color = Color.Unspecified,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(
                    color = if (selected) MaterialTheme.colorScheme.onSurface else Color.Unspecified,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(8.dp, 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = title,
                    modifier = Modifier.padding(start = 8.dp, end = 16.dp),
                    tint = if (selected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    maxLines = if (desc == null) 2 else 1,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                    color = if (selected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface,
                )
                desc?.let {
                    Text(
                        text = it,
                        color = if (selected) MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 2,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            action?.let {
                Box(Modifier.padding(start = 16.dp)) {
                    CompositionLocalProvider(
                        LocalContentColor provides (MaterialTheme.colorScheme.onSurface)
                    ) { it() }
                }
            }
        }
    }
}

@Composable
fun SettingItem(
    modifier: Modifier = Modifier,
    enable: Boolean = true,
    selected: Boolean = false,
    title: String,
    desc: String? = null,
    painter: Painter? = null,
    action: (@Composable () -> Unit)? = null,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier
            .clickable(onClick = onClick)
            .alpha(if (enable) 1f else 0.5f),
        color = Color.Unspecified,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(
                    color = if (selected) MaterialTheme.colorScheme.onSurface else Color.Unspecified,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(8.dp, 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            painter?.let {
                Icon(
                    painter = it,
                    contentDescription = title,
                    modifier = Modifier.padding(start = 8.dp, end = 16.dp).size(24.dp),
                    tint = if (selected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    maxLines = if (desc == null) 2 else 1,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                    color = if (selected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface,
                )
                desc?.let {
                    Text(
                        text = it,
                        color = if (selected) MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            action?.let {
                Box(Modifier.padding(start = 16.dp)) {
                    CompositionLocalProvider(
                        LocalContentColor provides (MaterialTheme.colorScheme.onSurface)
                    ) { it() }
                }
            }
        }
    }
}

@Composable
fun SettingItem(
    modifier: Modifier = Modifier,
    enable: Boolean = true,
    title: String,
    desc: String? = null,
    icon: ImageVector? = null,
    action: (@Composable () -> Unit)? = null,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = desc?.let { { Text(it) } },
        leadingContent = icon?.let { { Icon(it, title) } },
        trailingContent = action,
        modifier = modifier.clickable(
            enabled = enable,
            onClick = onClick,
        ),
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        )
    )
}

@Composable
fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun SettingItemPreview() {
    BandoriTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .background(MaterialTheme.colorScheme.surface),
        ) {
            MainCard(
                action = {
                    Text(
                        text = "30天",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            ) {}

            Text(
                text = "设置",
                style = MaterialTheme.typography.labelLargeEmphasized,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 32.dp, bottom = 8.dp)
            )

            SettingsGroup {
                SettingItem(
                    title = stringResource(Res.string.settings_encrypt_code_title),
                    desc = stringResource(Res.string.settings_encrypt_code_desc),
                    icon = Icons.Outlined.GroupAdd,
                    onClick = {}
                )

                SettingItem(
                    title = stringResource(Res.string.settings_encrypt_list_title),
                    desc = stringResource(Res.string.settings_encrypt_list_desc),
                    icon = Icons.AutoMirrored.Outlined.List,
                    onClick = {}
                )

                SettingItem(
                    title = stringResource(Res.string.settings_tutorial_title),
                    desc = stringResource(Res.string.settings_tutorial_desc),
                    icon = Icons.Outlined.School,
                    onClick = {}
                )

                SettingItem(
                    title = stringResource(Res.string.settings_version_title),
                    desc = "Android 35: 1.0.4-debug",
                    icon = Icons.Outlined.Update,
                    onClick = {}
                )
            }
        }
    }
}