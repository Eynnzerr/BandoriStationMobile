package com.eynnzerr.bandoristation.ui.dialog

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
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
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.dialog_close
import bandoristationm.composeapp.generated.resources.help_dialog_title
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun HelpDialog(
    isVisible: Boolean,
    markdownPath: String,
    onDismissRequest: () -> Unit,
) {
    val state = rememberRichTextState()
    var markdown by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(Unit) {
        markdown = Res.readBytes(markdownPath).decodeToString()
        state.setMarkdown(markdown)
    }

    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Help,
                    contentDescription = null,
                )
            },
            title = {
                Text(stringResource(Res.string.help_dialog_title))
            },
            text = {
                RichText(
                    state = state,
                    modifier = Modifier.verticalScroll(rememberScrollState())
                )
            },
            confirmButton = {
                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text(stringResource(Res.string.dialog_close))
                }
            },
        )
    }
}