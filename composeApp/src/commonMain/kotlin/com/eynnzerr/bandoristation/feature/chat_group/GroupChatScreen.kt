package com.eynnzerr.bandoristation.feature.chat_group

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eynnzerr.bandoristation.model.chat_group.ChatGroupMessage
import com.eynnzerr.bandoristation.model.chat_group.GroupMessageType
import com.eynnzerr.bandoristation.ui.component.chat.BottomTextBar
import com.eynnzerr.bandoristation.ui.component.chat_group.ChatPiece
import com.eynnzerr.bandoristation.ui.theme.BandoriTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun GroupChatScreen(
    onClose: () -> Unit,
    onQuit: () -> Unit,
    onSend: (String) -> Unit,
    onBrowseUser: (String) -> Unit,
    onRemoveUser: (String) -> Unit,
    groupName: String = "",
    messages: List<ChatGroupMessage>,
    selfId: Long,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = groupName) },
                navigationIcon = {
                    IconButton(
                        onClick = onClose,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = null,
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onQuit,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = null,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                )
            )
        },
        bottomBar = {
            BottomTextBar(
                onSend = onSend,
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
        ) {
            itemsIndexed(
                items = messages,
                key = { index, item -> "$index-${item.id}" }
            ) { _, chatMessage ->
                Row(
                    modifier = Modifier.animateItem()
                ) {
                    ChatPiece(
                        chatMessage = chatMessage,
                        isMyMessage = chatMessage.senderId == selfId.toString(),
                        onBrowseUser = onBrowseUser,
                        onRemoveUser = onRemoveUser,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun GroupChatScreenPreview() {
    val fakeMessages = listOf(
        ChatGroupMessage(
            id = 0,
            senderId = "1",
            content = "text test",
            username = "eynnzerr",
            avatar = "",
            createdAt = "",
            type = GroupMessageType.TEXT,
        ),
        ChatGroupMessage(
            id = 0,
            senderId = "2",
            content = "self text test",
            username = "eynnzerr",
            avatar = "",
            createdAt = "",
            type = GroupMessageType.TEXT,
        ),
        ChatGroupMessage(
            id = 0,
            senderId = "1",
            content = "system test",
            username = "eynnzerr",
            avatar = "",
            createdAt = "",
            type = GroupMessageType.SYSTEM,
        ),
    )
    BandoriTheme {
        GroupChatScreen(
            onClose = {},
            onQuit = {},
            onSend = {},
            onBrowseUser = {},
            onRemoveUser = {},
            messages = fakeMessages,
            selfId = 2,
            groupName = "192222"
        )
    }
}