package com.eynnzerr.bandoristation.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.RoomInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformDialog(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    selectedRoom: RoomInfo?,
    onConfirm: (ApiRequest.InformUser) -> Unit,
) {
    var reason by remember{ mutableStateOf("") }

    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            icon = {
                Icon(
                    imageVector = Icons.Default.Report,
                    contentDescription = null,
                )
            },
            title = {
                Text("举报")
            },
            text = {
                Column {
                    Text(
                        text = "确定举报用户“${selectedRoom?.userInfo?.username}”？",
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("理由（必填）") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4,
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = reason.isNotBlank(),
                    onClick = {
                        selectedRoom?.let { room ->
                            val params = ApiRequest.InformUser(
                                type = room.userInfo?.type ?: "",
                                userId = room.userInfo?.userId ?: 0,
                                rawMessage = room.rawMessage ?: "",
                                reason = reason,
                            )
                            onConfirm(params)
                        }
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