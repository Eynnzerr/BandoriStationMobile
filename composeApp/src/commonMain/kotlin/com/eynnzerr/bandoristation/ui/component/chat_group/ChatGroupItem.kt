package com.eynnzerr.bandoristation.ui.component.chat_group

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eynnzerr.bandoristation.model.chat_group.ChatGroupDetails
import com.eynnzerr.bandoristation.model.chat_group.OwnerInfo
import com.eynnzerr.bandoristation.ui.component.UserAvatar
import com.eynnzerr.bandoristation.ui.theme.BandoriTheme

@Composable
fun ChatGroupItem(details: ChatGroupDetails) {
    ListItem(
        headlineContent = { Text(details.name) },
        supportingContent = { Text("房主: ${details.owner.name}") },
        leadingContent = {
            UserAvatar(
                avatarName = details.owner.avatar,
                size = 48.dp,
                shape = RoundedCornerShape(4.dp)
            )
        },
        trailingContent = {
            TextButton(
                onClick = {},
                enabled = details.memberCount < 8,
            ) {
                Text("加入\n${details.memberCount} / 8")
            }
        },
    )
}

@Preview
@Composable
private fun ChatGroupItemPreview() {
    BandoriTheme {
        ChatGroupItem(ChatGroupDetails(
            id = "123",
            name = "聊天室一",
            owner = OwnerInfo(
                id = "123",
                name = "test",
                avatar = ""
            ),
            memberCount = 8,
            createdAt = "2023-08-01",
            lastActivityAt = "2023-08-01"
        ))
    }
}