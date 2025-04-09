package com.eynnzerr.bandoristation.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eynnzerr.bandoristation.model.ChatMessage
import com.eynnzerr.bandoristation.utils.mockChatList
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChatPiece(
    chatMessage: ChatMessage,
    isMyMessage: Boolean = true,
) {
    if (isMyMessage) {
        ChatBubble(
            text = chatMessage.content,
            isMyMessage = true,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    } else {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            UserAvatar(
                avatarName = chatMessage.userInfo.avatar ?: "",
                size = 56.dp
            )
            Column(
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
                    text = chatMessage.userInfo.username ?: "",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                ChatBubble(
                    text = chatMessage.content,
                    isMyMessage = false,
                )
            }
        }
    }
}

@Preview
@Composable
private fun ChatPiecePreview() {
    Surface {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            mockChatList.forEachIndexed { index, message ->
                ChatPiece(
                    chatMessage = message,
                    isMyMessage = index % 2 == 0
                )
            }
        }
    }
}