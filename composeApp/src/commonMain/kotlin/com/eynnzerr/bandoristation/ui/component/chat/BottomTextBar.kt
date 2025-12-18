package com.eynnzerr.bandoristation.ui.component.chat

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.chat_message_input_placeholder
import com.eynnzerr.bandoristation.ui.theme.BandoriTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun BottomTextBar(
    modifier: Modifier = Modifier,
    onSend: (String) -> Unit,
) {
    var messageText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        focusManager.clearFocus()
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = messageText,
            onValueChange = { messageText = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text(stringResource(Res.string.chat_message_input_placeholder)) },
            maxLines = 3
        )
        FilledIconButton(
            onClick = {
                onSend(messageText)
                messageText = ""
            },
            modifier = Modifier.padding(start = 8.dp),
            enabled = messageText.isNotBlank()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = ""
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomTextBarPreview() {
    BandoriTheme {
        BottomTextBar(
            onSend = {}
        )
    }
}