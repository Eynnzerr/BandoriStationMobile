package com.eynnzerr.bandoristation.ui.component.chat_group

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eynnzerr.bandoristation.model.chat_group.ChatGroupMessage
import com.eynnzerr.bandoristation.model.chat_group.GroupMessageType
import com.eynnzerr.bandoristation.ui.component.UserAvatar
import com.eynnzerr.bandoristation.ui.component.chat.ChatBubble
import com.eynnzerr.bandoristation.ui.component.chat.SystemPiece

@Composable
fun ChatPiece(
    chatMessage: ChatGroupMessage,
    onBrowseUser: (String) -> Unit = {},
    onRemoveUser: (String) -> Unit = {},
    isMyMessage: Boolean = true,
) {
    when (chatMessage.type) {
        GroupMessageType.TEXT -> {
            ChatPiece(
                message = chatMessage.content,
                username = chatMessage.username,
                avatar = chatMessage.avatar,
                isMyMessage = isMyMessage,
                onBrowseUser = { onBrowseUser(chatMessage.senderId) },
                onRemoveUser = { onRemoveUser(chatMessage.senderId) },
            )
        }
        GroupMessageType.SYSTEM -> {
            SystemPiece(chatMessage.content)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatPiece(
    message: String,
    username: String,
    avatar: String,
    isMyMessage: Boolean = true,
    onBrowseUser: () -> Unit = {},
    onRemoveUser: () -> Unit = {},
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
                avatarName = avatar,
                size = 56.dp,
                tooltipContent = {
                    RichTooltip {
                        Column {
                            TextButton(
                                onClick = onBrowseUser
                            ) {
                                Text("查看资料")
                            }

                            TextButton(
                                onClick = onRemoveUser
                            ) {
                                Text("移出群聊")
                            }
                        }
                    }
                }
            )
            Spacer(Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = columnHorizontalAlignment
        ) {
            Text(
                text = username,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = if (isMyMessage) Modifier.padding(end = 8.dp) else Modifier.padding(start = 8.dp)
            )
            Spacer(Modifier.height(4.dp))
            ChatBubble(
                text = message.trimEnd(),
                isMyMessage = isMyMessage,
            )
        }

        if (isMyMessage) {
            Spacer(Modifier.width(8.dp))
            UserAvatar(
                avatarName = avatar,
                size = 56.dp,
                onClick = {},
            )
        }
    }
}