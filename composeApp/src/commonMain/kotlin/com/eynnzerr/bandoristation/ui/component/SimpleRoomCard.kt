package com.eynnzerr.bandoristation.ui.component

import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eynnzerr.bandoristation.model.RoomInfo
import kotlin.time.Clock.System
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun SimpleRoomCard(
    roomInfo: RoomInfo,
    onCopy: (String) -> Unit,
    currentTimeMillis: Long = System.now().toEpochMilliseconds(),
) {
    // 添加相对时间显示
    val timeDiffMillis = currentTimeMillis - (roomInfo.time ?: currentTimeMillis)
    val timeDiffSeconds = (timeDiffMillis / 1000).toInt()

    val timeText = if (timeDiffSeconds < 60) {
        "${timeDiffSeconds}秒前"
    } else {
        "${(timeDiffSeconds / 60)}分钟前"
    }

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onCopy(roomInfo.number ?: "") }
                )
            },
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
                    Text(
                        text = (if (roomInfo.sourceInfo?.name == "Tsugu") "来自茨菇" else "来自本站") + timeText,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
        }

        Text(
            text = roomInfo.rawMessage ?: "",
            style = MaterialTheme.typography.bodyLarge,
            overflow = TextOverflow.Ellipsis,
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 12.dp, bottom = 12.dp)
        )
    }
}