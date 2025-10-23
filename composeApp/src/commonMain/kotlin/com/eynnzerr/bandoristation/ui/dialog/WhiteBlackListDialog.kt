package com.eynnzerr.bandoristation.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.dialog_confirm
import com.eynnzerr.bandoristation.ui.component.PlainButton
import com.eynnzerr.bandoristation.ui.component.WhiteBlackListItem
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

private enum class WhiteBlackListDialogState {
    SELECTION,
    BLACKLIST,
    WHITELIST
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhiteBlackListDialog(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    blacklist: List<String>,
    whitelist: List<String>,
    onRemoveFromBlackList: (String) -> Unit,
    onRemoveFromWhiteList: (String) -> Unit,
    onViewUserInfo: (String) -> Unit,
    placeholder: @Composable () -> Unit = {},
) {
    if (isVisible) {
        var state by remember { mutableStateOf(WhiteBlackListDialogState.SELECTION) }

        AlertDialog(
            onDismissRequest = onDismissRequest,
            icon = {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = null
                )
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (state != WhiteBlackListDialogState.SELECTION) {
                        IconButton(onClick = { state = WhiteBlackListDialogState.SELECTION }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "back"
                            )
                        }
                    }
                    Text(
                        text = when (state) {
                            WhiteBlackListDialogState.SELECTION -> "黑白名单管理"
                            WhiteBlackListDialogState.BLACKLIST -> "黑名单"
                            WhiteBlackListDialogState.WHITELIST -> "白名单"
                        }
                    )
                }
            },
            text = {
                when (state) {
                    WhiteBlackListDialogState.SELECTION -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                PlainButton(
                                    onClick = { state = WhiteBlackListDialogState.BLACKLIST },
                                    imageVector = Icons.Default.People,
                                    text = "查看黑名单"
                                )
                                PlainButton(
                                    onClick = { state = WhiteBlackListDialogState.WHITELIST },
                                    imageVector = Icons.Outlined.People,
                                    text = "查看白名单"
                                )
                            }

                            Text(
                                text = "* 由于车站服务器未提供批量查询接口，出于APP性能考虑，在具体名单界面不获取展示用户信息，请点击头像查看具体用户。",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    WhiteBlackListDialogState.BLACKLIST -> {
                        if (blacklist.isEmpty()) {
                            placeholder()
                        } else {
                            LazyColumn {
                                items(
                                    items = blacklist,
                                    key = { it }
                                ) {
                                    WhiteBlackListItem(
                                        avatar = "",
                                        username = it,
                                        introduction = "",
                                        onRemove = {
                                            onRemoveFromBlackList(it)
                                        },
                                        onView = {
                                            onViewUserInfo(it)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    WhiteBlackListDialogState.WHITELIST -> {
                        if (whitelist.isEmpty()) {
                            placeholder()
                        } else {
                            LazyColumn {
                                items(
                                    items = whitelist,
                                    key = { it }
                                ) {
                                    WhiteBlackListItem(
                                        avatar = "",
                                        username = it,
                                        introduction = "",
                                        onRemove = {
                                            onRemoveFromWhiteList(it)
                                        },
                                        onView = {
                                            onViewUserInfo(it)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text(stringResource(Res.string.dialog_confirm))
                }
            },
            dismissButton = {},
        )
    }
}

@Composable
@Preview
fun PreviewWhiteBlackListDialog() {
    WhiteBlackListDialog(
        isVisible = true,
        onDismissRequest = {},
        blacklist = listOf(),
        whitelist = listOf(),
        onRemoveFromBlackList = {},
        onRemoveFromWhiteList = {},
        onViewUserInfo = {},
        placeholder = {
            Text("暂无数据")
        }
    )
}
