package com.eynnzerr.bandoristation.ui.component.chat

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eynnzerr.bandoristation.model.ChatMessage

@Composable
fun SystemPiece(
    chatMessage: ChatMessage,
) {
    SystemPiece(chatMessage.content)
}

@Composable
fun SystemPiece(
    content: String,
) {
    Text(
        text = content.trim(),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}