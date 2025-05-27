package com.eynnzerr.bandoristation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.joined
import com.eynnzerr.bandoristation.model.RoomInfo
import com.eynnzerr.bandoristation.model.SourceInfo
import com.eynnzerr.bandoristation.model.UserInfo
import com.eynnzerr.bandoristation.utils.formatTimeDifference
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlinx.datetime.Clock

@Composable
fun CurrentRoomHeader(
    roomInfo: RoomInfo,
    onCopy: (String) -> Unit,
    onPublish: () -> Unit,
    onLeave: () -> Unit,
    currentTimeMillis: Long = Clock.System.now().toEpochMilliseconds(),
    startTimeMillis: Long = Clock.System.now().toEpochMilliseconds(),
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = stringResource(Res.string.joined),
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.labelMedium,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = roomInfo.number ?: "000000",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Room description
                    Text(
                        text = roomInfo.rawMessage ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Text(
                        text = "已上车 " + formatTimeDifference(currentTimeMillis, startTimeMillis),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Right side with action buttons
                Row {
                    // Copy button
                    IconButton(
                        onClick = { onCopy(roomInfo.number ?: "") },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Room Number",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Share button
                    IconButton(
                        onClick = onPublish,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Publish,
                            contentDescription = "Share Room",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = onLeave,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Leave Room",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun CurrentRoomHeaderPreview() {
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

    CurrentRoomHeader(mockRoom, {}, {}, {})
}