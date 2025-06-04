package com.eynnzerr.bandoristation.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eynnzerr.bandoristation.model.GithubRelease

@Composable
fun UpdateDialog(
    isVisible: Boolean,
    release: GithubRelease?,
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    if (isVisible && release != null) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            icon = {
                Icon(
                    imageVector = Icons.Default.SystemUpdate,
                    contentDescription = null,
                )
            },
            title = {
                Text("发现新版本 ${release.tagName}")
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(release.name)
                    Text(
                        text = release.body,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { onConfirm(release.htmlUrl) }) {
                    Text("前往下载")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) { Text("稍后") }
            }
        )
    }
}
