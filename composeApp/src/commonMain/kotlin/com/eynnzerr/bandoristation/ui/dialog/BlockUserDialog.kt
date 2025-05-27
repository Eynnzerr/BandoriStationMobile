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
                Text("屏蔽")
            },
            text = {
                Text(
                    text = "确定屏蔽用户“${selectedUser?.username}”？",
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm()
                        onDismissRequest()
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text("取消")
                }
            },
        )
    }
}