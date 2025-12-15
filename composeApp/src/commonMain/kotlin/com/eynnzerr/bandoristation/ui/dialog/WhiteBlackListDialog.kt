package com.eynnzerr.bandoristation.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.approve_request_dialog_add_to_blacklist
import bandoristationm.composeapp.generated.resources.approve_request_dialog_add_to_whitelist
import bandoristationm.composeapp.generated.resources.dialog_back_button_desc
import bandoristationm.composeapp.generated.resources.dialog_confirm
import bandoristationm.composeapp.generated.resources.wb_list_admin_icon_desc
import bandoristationm.composeapp.generated.resources.wb_list_dialog_blacklist
import bandoristationm.composeapp.generated.resources.wb_list_dialog_helper_text
import bandoristationm.composeapp.generated.resources.wb_list_dialog_no_data
import bandoristationm.composeapp.generated.resources.wb_list_dialog_title
import bandoristationm.composeapp.generated.resources.wb_list_dialog_whitelist
import com.eynnzerr.bandoristation.ui.component.PlainButton
import com.eynnzerr.bandoristation.ui.component.WhiteBlackListItem
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
                    contentDescription = stringResource(Res.string.wb_list_admin_icon_desc)
                )
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (state != WhiteBlackListDialogState.SELECTION) {
                        IconButton(onClick = { state = WhiteBlackListDialogState.SELECTION }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(Res.string.dialog_back_button_desc)
                            )
                        }
                    }
                    Text(
                        text = when (state) {
                            WhiteBlackListDialogState.SELECTION -> stringResource(Res.string.wb_list_dialog_title)
                            WhiteBlackListDialogState.BLACKLIST -> stringResource(Res.string.wb_list_dialog_blacklist)
                            WhiteBlackListDialogState.WHITELIST -> stringResource(Res.string.wb_list_dialog_whitelist)
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                PlainButton(
                                    onClick = { state = WhiteBlackListDialogState.BLACKLIST },
                                    imageVector = Icons.Default.People,
                                    text = stringResource(Res.string.approve_request_dialog_add_to_blacklist)
                                )
                                PlainButton(
                                    onClick = { state = WhiteBlackListDialogState.WHITELIST },
                                    imageVector = Icons.Outlined.People,
                                    text = stringResource(Res.string.approve_request_dialog_add_to_whitelist)
                                )
                            }

                            Text(
                                text = stringResource(Res.string.wb_list_dialog_helper_text),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(horizontal = 12.dp)
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
            Text(stringResource(Res.string.wb_list_dialog_no_data))
        }
    )
}
