package com.eynnzerr.bandoristation.ui.dialog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.create_chat_group_dialog_label
import bandoristationm.composeapp.generated.resources.create_chat_group_dialog_title
import bandoristationm.composeapp.generated.resources.create_chat_group_icon_desc
import bandoristationm.composeapp.generated.resources.dialog_cancel
import bandoristationm.composeapp.generated.resources.dialog_confirm
import org.jetbrains.compose.resources.stringResource

@Composable
fun CreateChatGroupDialog(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.AddCircleOutline,
                    contentDescription = stringResource(Res.string.create_chat_group_icon_desc)
                )
            },
            title = {
                Text(stringResource(Res.string.create_chat_group_dialog_title))
            },
            text = {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(Res.string.create_chat_group_dialog_label)) },
                    singleLine = true,
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm(name)
                        onDismissRequest()
                    },
                    enabled = name.isNotBlank()
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