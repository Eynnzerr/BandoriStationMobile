package com.eynnzerr.bandoristation.ui.dialog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.eynnzerr.bandoristation.model.UserInfo
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.block_user_dialog_title
import bandoristationm.composeapp.generated.resources.block_user_dialog_confirmation_message
import bandoristationm.composeapp.generated.resources.dialog_cancel
import bandoristationm.composeapp.generated.resources.dialog_confirm
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockUserDialog(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    selectedUser: UserInfo?,
    onConfirm: () -> Unit,
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            icon = {
                Icon(
                    imageVector = Icons.Default.Block,
                    contentDescription = null,
                )
            },
            title = {
                Text(stringResource(Res.string.block_user_dialog_title))
            },
            text = {
                Text(
                    text = stringResource(Res.string.block_user_dialog_confirmation_message, selectedUser?.username ?: ""),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm()
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
            },
        )
    }
}