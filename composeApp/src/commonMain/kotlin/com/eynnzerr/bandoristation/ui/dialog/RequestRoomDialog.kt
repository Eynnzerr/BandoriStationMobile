package com.eynnzerr.bandoristation.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bandoristationm.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview

enum class RequestRoomState {
    INITIAL,
    PENDING_APPROVAL,
    SUCCESS,
    ERROR
}

@Composable
fun RequestRoomDialog(
    isVisible: Boolean,
    state: RequestRoomState,
    roomNumber: String?,
    errorMessage: String?,
    onDismissRequest: () -> Unit,
    onSubmitInviteCode: (code: String) -> Unit,
    onApplyOnline: () -> Unit,
    onCopy: (String) -> Unit
) {
    var inviteCode by remember { mutableStateOf("") }

    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            icon = { Icon(imageVector = Icons.Default.LockOpen, contentDescription = null) },
            title = { Text(stringResource(Res.string.request_room_dialog_title)) },
            text = {
                when (state) {
                    RequestRoomState.INITIAL -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(stringResource(Res.string.request_room_dialog_prompt))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = inviteCode,
                                    onValueChange = { inviteCode = it },
                                    label = { Text(stringResource(Res.string.request_room_dialog_invite_code_label)) },
                                    modifier = Modifier.weight(1f),
                                    trailingIcon = {
                                        IconButton(
                                            onClick = {
                                                onSubmitInviteCode(inviteCode)
                                            }
                                        ) {
                                            Icon(Icons.AutoMirrored.Filled.Send, null)
                                        }
                                    }
                                )
                            }
                            Text(
                                text = "- 或 -",
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                            )
                            Button(
                                onClick = onApplyOnline,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(Res.string.request_room_dialog_apply_online_button))
                            }
                        }
                    }
                    RequestRoomState.PENDING_APPROVAL -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            LoadingIndicator(
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                            Text(
                                text = stringResource(Res.string.request_room_dialog_pending_text),
                                style = MaterialTheme.typography.bodyLargeEmphasized,
                            )
                        }
                    }
                    RequestRoomState.SUCCESS -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(Res.string.request_room_dialog_success_text), style = MaterialTheme.typography.bodyLarge)
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(roomNumber ?: "", style = MaterialTheme.typography.headlineMediumEmphasized)
                                IconButton(onClick = { onCopy(roomNumber ?: "") }) {
                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = stringResource(Res.string.request_room_dialog_copy_button))
                                }
                            }
                        }
                    }
                    RequestRoomState.ERROR -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = errorMessage ?: stringResource(Res.string.request_room_dialog_error_unknown),
                                style = MaterialTheme.typography.bodyLargeEmphasized,
                            )
                        }
                    }
                }
            },
            confirmButton = {
                if (state == RequestRoomState.ERROR) {
                    TextButton(onClick = onDismissRequest) {
                        Text(stringResource(Res.string.request_room_dialog_retry_button))
                    }
                }
            },
            dismissButton = {
                if (state == RequestRoomState.INITIAL || state == RequestRoomState.PENDING_APPROVAL || state == RequestRoomState.SUCCESS) {
                    TextButton(onClick = onDismissRequest) {
                        Text(stringResource(Res.string.request_room_dialog_cancel_button))
                    }
                }
            }
        )
    }
}

@Preview
@Composable
private fun RequestRoomDialogPreviewInitial() {
    RequestRoomDialog(
        isVisible = true,
        state = RequestRoomState.INITIAL,
        roomNumber = null,
        errorMessage = null,
        onDismissRequest = {},
        onSubmitInviteCode = {},
        onApplyOnline = {},
        onCopy = {}
    )
}

@Preview
@Composable
private fun RequestRoomDialogPreviewPending() {
    RequestRoomDialog(
        isVisible = true,
        state = RequestRoomState.PENDING_APPROVAL,
        roomNumber = null,
        errorMessage = null,
        onDismissRequest = {},
        onSubmitInviteCode = {},
        onApplyOnline = {},
        onCopy = {}
    )
}

@Preview
@Composable
private fun RequestRoomDialogPreviewSuccess() {
    RequestRoomDialog(
        isVisible = true,
        state = RequestRoomState.SUCCESS,
        roomNumber = "123456",
        errorMessage = null,
        onDismissRequest = {},
        onSubmitInviteCode = {},
        onApplyOnline = {},
        onCopy = {}
    )
}

@Preview
@Composable
private fun RequestRoomDialogPreviewError() {
    RequestRoomDialog(
        isVisible = true,
        state = RequestRoomState.ERROR,
        roomNumber = null,
        errorMessage = "Incorrect invitation code.",
        onDismissRequest = {},
        onSubmitInviteCode = {},
        onApplyOnline = {},
        onCopy = {}
    )
}
