package com.eynnzerr.bandoristation.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.dialog_cancel
import bandoristationm.composeapp.generated.resources.dialog_confirm
import bandoristationm.composeapp.generated.resources.invitation_code_copied_icon_desc
import bandoristationm.composeapp.generated.resources.invitation_code_copy_icon_desc
import bandoristationm.composeapp.generated.resources.invitation_code_dialog_label
import bandoristationm.composeapp.generated.resources.invitation_code_icon_desc
import bandoristationm.composeapp.generated.resources.settings_encrypt_code_helper_text
import bandoristationm.composeapp.generated.resources.settings_encrypt_code_title
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun InvitationCodeDialog(
    isVisible: Boolean,
    initialCode: String,
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var code by remember(initialCode) { mutableStateOf(initialCode) }
    var isCopied by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(2000)
            isCopied = false
        }
    }

    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.GroupAdd,
                    contentDescription = stringResource(Res.string.invitation_code_icon_desc)
                )
            },
            title = {
                Text(stringResource(Res.string.settings_encrypt_code_title))
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        label = { Text(stringResource(Res.string.invitation_code_dialog_label)) },
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = {
                                clipboardManager.setText(AnnotatedString(code))
                                isCopied = true
                            }) {
                                Icon(
                                    imageVector = if (isCopied) Icons.Filled.Check else Icons.Filled.ContentCopy,
                                    contentDescription = if (isCopied) stringResource(Res.string.invitation_code_copied_icon_desc) else stringResource(Res.string.invitation_code_copy_icon_desc)
                                )
                            }
                        }
                    )
                    Text(
                        text = stringResource(Res.string.settings_encrypt_code_helper_text),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm(code)
                        onDismissRequest()
                    }
                ) {
                    Text(stringResource(Res.string.dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(Res.string.dialog_cancel))
                }
            }
        )
    }
}

@Preview
@Composable
fun InvitationCodeDialogPreview() {
    InvitationCodeDialog(
        isVisible = true,
        initialCode = "eynnzerr",
        onDismissRequest = {},
        onConfirm = {},
    )
}