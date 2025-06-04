package com.eynnzerr.bandoristation.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bandoristationm.composeapp.generated.resources.Res
import com.eynnzerr.bandoristation.model.GithubRelease
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText

@Composable
fun UpdateDialog(
    isVisible: Boolean,
    release: GithubRelease?,
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    if (isVisible && release != null) {
        val state = rememberRichTextState()
        var markdown by rememberSaveable { mutableStateOf("") }
        LaunchedEffect(Unit) {
            markdown = release.body
            state.setMarkdown(markdown)
        }

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
                RichText(
                    state = state,
                    modifier = Modifier.verticalScroll(rememberScrollState())
                )
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
