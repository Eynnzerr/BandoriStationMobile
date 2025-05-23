package com.eynnzerr.bandoristation.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FollowerItem(
    modifier: Modifier = Modifier,
    avatar: String = "",
    username: String = "",
    introduction: String = "",
    hasFollowed: Boolean = false,
    onFollow: () -> Unit,
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UserAvatar(
                avatarName = avatar,
                size = 48.dp
            )
            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = username,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 18.sp,
                    )
                }
                Text(
                    text = introduction,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 16.sp,
                )
            }

            Button(
                onClick = onFollow,
                shape = MaterialTheme.shapes.medium,
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = if (hasFollowed) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer,
//                )
            ) {
                Text(if (hasFollowed) "取关" else "关注")
            }
        }
    }
}

@Preview
@Composable
private fun FollowerItemPreview() {
    FollowerItem(
        avatar = "",
        username = "Arisa",
        introduction = "I love Kasumi",
        hasFollowed = true,
        onFollow = {}
    )
}