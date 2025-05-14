package com.eynnzerr.bandoristation.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eynnzerr.bandoristation.model.RoomInfo
import com.eynnzerr.bandoristation.model.SourceInfo
import com.eynnzerr.bandoristation.model.UserInfo
import com.eynnzerr.bandoristation.utils.AppLogger
import com.eynnzerr.bandoristation.utils.formatTimeDifference
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock.System
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, ExperimentalFoundationApi::class)
@Composable
fun RoomCard(
    roomInfo: RoomInfo,
    onCopy: (String) -> Unit,
    onJoin: (Boolean) -> Unit,
    onBlockUser: () -> Unit,
    onReportUser: () -> Unit,
    isJoined: Boolean,
    currentTimeMillis: Long = System.now().toEpochMilliseconds(),
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .combinedClickable(
                onClick = {  },
                onLongClick = { onCopy(roomInfo.number ?: "") },
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UserAvatar(
                avatarName = roomInfo.userInfo?.avatar ?: "",
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
                        text = roomInfo.userInfo?.username ?: "???",
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 18.sp,
                    )
                }
                Text(
                    text = roomInfo.number ?: "000000",
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 16.sp,
                )
            }

            FilledIconToggleButton(
                checked = isJoined,
                onCheckedChange = { onJoin(it) }
            ) {
                if (isJoined) {
                    Icon(Icons.Filled.DirectionsBus, contentDescription = "")
                } else {
                    Icon(Icons.Outlined.DirectionsBus, contentDescription = "")
                }
            }
        }

        Text(
            text = roomInfo.rawMessage ?: "",
            style = MaterialTheme.typography.bodyLarge,
            overflow = TextOverflow.Ellipsis,
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 12.dp)
        )

        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatTimeDifference(currentTimeMillis, roomInfo.time ?: currentTimeMillis) + "前",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 8.dp)
            )

            Text(
                text = if (roomInfo.sourceInfo?.name == "Tsugu") "来自茨菇" else "来自本站", // TODO 需要研究一下判定条件
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )

            TextButton(
                onClick = onBlockUser,
            ) {
                Text(
                    text = "屏蔽",
                )
            }

            TextButton(
                onClick = onReportUser,
            ) {
                Text(
                    text = "举报",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview
@Composable
fun RoomCardPreview() {
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

    var isJoined by remember { mutableStateOf(false) }

    RoomCard(
        roomInfo = mockRoom,
        onCopy = { number ->
            AppLogger.d(TAG, "onCopy: $number")
        },
        onJoin = { join ->
            AppLogger.d(TAG, "onMark: $join")
            isJoined = join
        },
        onBlockUser = {},
        onReportUser = {},
        isJoined = isJoined
    )
}

private const val TAG = "RoomCard"