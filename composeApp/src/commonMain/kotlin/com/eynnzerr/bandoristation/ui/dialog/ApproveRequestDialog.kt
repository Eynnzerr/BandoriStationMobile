package com.eynnzerr.bandoristation.ui.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonSearch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import bandoristationm.composeapp.generated.resources.approve_request_dialog_agree_button
import bandoristationm.composeapp.generated.resources.approve_request_dialog_refuse_button
import bandoristationm.composeapp.generated.resources.approve_request_dialog_title
import com.eynnzerr.bandoristation.model.room.RoomAccessRequest
import com.eynnzerr.bandoristation.ui.component.UserAvatar
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ApproveRequestDialog(
    isVisible: Boolean,
    request: RoomAccessRequest?,
    onDismissRequest: (addToWhitelist: Boolean, addToBlacklist: Boolean) -> Unit,
    onConfirm: (addToWhitelist: Boolean, addToBlacklist: Boolean) -> Unit,
    onViewUser: () -> Unit,
) {
    var countdown by remember(request) { mutableStateOf(30) }
    var addToWhitelist by remember(request) { mutableStateOf(false) }
    var addToBlacklist by remember(request) { mutableStateOf(false) }

    LaunchedEffect(request) {
        if (request != null) {
            countdown = 30
            addToWhitelist = false
            addToBlacklist = false
            while (countdown > 0) {
                delay(1000)
                countdown--
            }
            if (countdown == 0) {
                onDismissRequest(addToWhitelist, addToBlacklist) // Timeout is a refusal
            }
        }
    }

    if (isVisible && request != null) {
        AlertDialog(
            onDismissRequest = {
                onDismissRequest(addToWhitelist, addToBlacklist)
            },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.PersonSearch,
                    contentDescription = null
                )
            },
            title = {
                Text(stringResource(Res.string.approve_request_dialog_title))
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    UserAvatar(
                        avatarName = request.requesterAvatar,
                        size = 64.dp,
                        onClick = onViewUser
                    )
                    Text(
                        text = request.requesterName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Row(
                            modifier = Modifier.clickable {
                                addToWhitelist = !addToWhitelist
                                if (addToWhitelist) addToBlacklist = false
                            },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Checkbox(
                                checked = addToWhitelist,
                                onCheckedChange = {
                                    addToWhitelist = it
                                    if (it) addToBlacklist = false // Cannot be both
                                }
                            )
                            Text(stringResource(Res.string.approve_request_dialog_add_to_whitelist))
                        }

                        Row(
                            modifier = Modifier.clickable {
                                addToBlacklist = !addToBlacklist
                                if (addToBlacklist) addToWhitelist = false
                            },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Checkbox(
                                checked = addToBlacklist,
                                onCheckedChange = {
                                    addToBlacklist = it
                                    if (it) addToWhitelist = false // Cannot be both
                                }
                            )
                            Text(stringResource(Res.string.approve_request_dialog_add_to_blacklist))
                        }

                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm(addToWhitelist, addToBlacklist)
                    }
                ) {
                    Text(stringResource(Res.string.approve_request_dialog_agree_button, countdown))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismissRequest(addToWhitelist, addToBlacklist)
                    }
                ) {
                    Text(stringResource(Res.string.approve_request_dialog_refuse_button))
                }
            }
        )
    }
}

@Composable
@Preview
private fun ApproveRequestDialogPreview() {
    ApproveRequestDialog(
        isVisible = true,
        request = RoomAccessRequest(
            requestId = "1",
            targetUserId = "1",
            requesterId = "2",
            requesterName = "Eynnzerr",
            requesterAvatar = "https://asset.bandoristation.com/images/user-avatar/3aa3b60a62901ae7a8bc49117c4dbd42.png"
        ),
        onDismissRequest = { _, _ -> },
        onConfirm = { _, _ -> },
        onViewUser = {},
    )
}
