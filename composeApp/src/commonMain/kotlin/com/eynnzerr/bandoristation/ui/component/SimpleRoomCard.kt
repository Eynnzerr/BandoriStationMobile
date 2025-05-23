package com.eynnzerr.bandoristation.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eynnzerr.bandoristation.model.RoomInfo
import com.eynnzerr.bandoristation.model.SourceInfo
import com.eynnzerr.bandoristation.model.UserInfo
import com.eynnzerr.bandoristation.utils.formatTimeDifference
import com.eynnzerr.bandoristation.utils.formatTimestamp
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, ExperimentalFoundationApi::class)
@Composable
fun SimpleRoomCard(
    number: String = "",
    rawMessage: String = "",
    timestamp: Long = 0,
    sourceName: String = "",
    avatarName: String = "",
    userName: String = "",
    onCopy: (String) -> Unit,
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .combinedClickable(
                onClick = {  },
                onLongClick = { onCopy(number) },
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UserAvatar(
                avatarName = avatarName,
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
                        text = userName,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 18.sp,
                    )
                    Text(
                        text = (if (sourceName == "BandoriStation") "来自本站" else "来自Bot"),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = number,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 16.sp,
                )
            }
        }

        Text(
            text = rawMessage,
            style = MaterialTheme.typography.bodyLarge,
            overflow = TextOverflow.Ellipsis,
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 12.dp)
        )

        Text(
            text = formatTimestamp(timestamp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Preview
@Composable
private fun SimpleRoomCardPreview() {
    val mockRoom = RoomInfo(
        number = "114514",
        rawMessage = "114514 5w 130+ 大e长 禁hdfc 欢迎清火 q1",
        sourceInfo = SourceInfo(
            name = "Tsugu",
            type = "qq"
        ),
        type = "other",
        time = 1743581164274,
        userInfo = UserInfo(
            type = "local",
            userId = 8146,
            username = "Tsugu代发",
            avatar = "7ec6cbbc94d098d96b291ab4955baa7a.png",
            role = 0,
            playerBriefInfo = null,
        )
    )

    SimpleRoomCard(
        number = mockRoom.number ?: "",
        rawMessage = mockRoom.rawMessage ?: "",
        timestamp = mockRoom.time ?: 0,
        sourceName = mockRoom.sourceInfo?.name ?: "",
        avatarName = mockRoom.userInfo?.avatar ?: "",
        userName = mockRoom.userInfo?.username ?: "",
        onCopy = {}
    )
}