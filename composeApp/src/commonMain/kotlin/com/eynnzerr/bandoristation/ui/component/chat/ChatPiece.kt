package com.eynnzerr.bandoristation.ui.component.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.eynnzerr.bandoristation.ui.component.UserAvatar
import com.eynnzerr.bandoristation.utils.mockChatList
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ChatPiece(
    chatMessage: ChatMessage,
    isMyMessage: Boolean = true,
    onClickAvatar: () -> Unit = {},
) {
    val horizontalArrangement = if (isMyMessage) Arrangement.End else Arrangement.Start
    val columnHorizontalAlignment = if (isMyMessage) Alignment.End else Alignment.Start

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.Top
    ) {
        if (!isMyMessage) {
            UserAvatar(
                avatarName = chatMessage.userInfo.avatar ?: "",
                size = 56.dp,
                onClick = onClickAvatar,
            )
            Spacer(Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.weight(1f), // Add weight to push avatar to side
            horizontalAlignment = columnHorizontalAlignment
        ) {
            Text(
                text = chatMessage.userInfo.username ?: "",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = if (isMyMessage) Modifier.padding(end = 8.dp) else Modifier.padding(start = 8.dp)
            )
            Spacer(Modifier.height(4.dp))
            ChatBubble(
                text = chatMessage.content.trimEnd(),
                isMyMessage = isMyMessage,
            )
        }

        if (isMyMessage) {
            Spacer(Modifier.width(8.dp))
            UserAvatar(
                avatarName = chatMessage.userInfo.avatar ?: "",
                size = 56.dp,
                onClick = onClickAvatar,
            )
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