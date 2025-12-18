package com.eynnzerr.bandoristation.ui.component.chat_group

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.chat_group_join
import bandoristationm.composeapp.generated.resources.chat_group_owner
import com.eynnzerr.bandoristation.model.chat_group.ChatGroupDetails
import com.eynnzerr.bandoristation.model.chat_group.SimpleUserInfo
import com.eynnzerr.bandoristation.ui.component.UserAvatar
import com.eynnzerr.bandoristation.ui.theme.BandoriTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun ChatGroupItem(
    details: ChatGroupDetails,
    onJoin: (String) -> Unit,
) {
    ListItem(
        headlineContent = { Text(details.name) },
        supportingContent = { Text(stringResource(Res.string.chat_group_owner, details.owner.name)) },
        leadingContent = {
            UserAvatar(
                avatarName = details.owner.avatar,
                size = 48.dp,
                shape = RoundedCornerShape(4.dp)
            )
        },
        trailingContent = {
            TextButton(
                onClick = { onJoin(details.owner.id) },
                enabled = details.memberCount < 8,
            ) {
                Text(stringResource(Res.string.chat_group_join, details.memberCount))
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
            owner = SimpleUserInfo(
                id = "123",
                name = "test",
                avatar = ""
            ),
            memberCount = 8,
            createdAt = "2023-08-01",
            lastActivityAt = "2023-08-01"
        )) {}
    }
}