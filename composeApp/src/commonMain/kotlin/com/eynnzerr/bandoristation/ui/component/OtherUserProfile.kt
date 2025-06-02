package com.eynnzerr.bandoristation.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eynnzerr.bandoristation.model.account.AccountInfo

@Composable
fun OtherUserProfile(
    modifier: Modifier = Modifier,
    accountInfo: AccountInfo,
    actionButton: @Composable () -> Unit = {},
    sideButton: @Composable () -> Unit = {},
    onBrowseFollowers: () -> Unit = {},
    onBrowseFollowings: () -> Unit = {},
) {
    val tabs = listOf("发车历史")
    var selectedTabIndex by remember { mutableStateOf(0) }

    Column(
        modifier = modifier,
    ) {
        Box(
            contentAlignment = Alignment.TopEnd,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            UserBannerImage(
                bannerName = accountInfo.accountSummary.banner,
                modifier = Modifier.fillMaxSize()
            )

            actionButton()
        }

        // 用户信息区域
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 用户头像
                UserAvatar(
                    avatarName = accountInfo.accountSummary.avatar,
                    size = 64.dp
                )

                // 用户信息
                Column(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .padding(top = 4.dp)
                ) {
                    Text(
                        text = accountInfo.accountSummary.username,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "UID: ${accountInfo.accountSummary.userId}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                    Row(
                        modifier = Modifier.padding(top = 0.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "${accountInfo.accountSummary.following} 关注",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable(onClick = onBrowseFollowings)
                        )
                        Text(
                            text = "${accountInfo.accountSummary.follower} 关注者",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable(onClick = onBrowseFollowers)
                        )
                    }
                }
            }

            sideButton()
        }

        Text(
            text = accountInfo.accountSummary.introduction,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            modifier = Modifier
                .padding(top = 4.dp)
                .padding(horizontal = 16.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 标签页
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        // 标签页内容
        when (selectedTabIndex) {
            0 -> {
                // 发车历史
                if (accountInfo.roomNumberHistory.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "无最近发车记录",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        itemsIndexed(accountInfo.roomNumberHistory) { index, roomInfo ->
                            SimpleRoomCard(
                                number = roomInfo.number,
                                rawMessage = roomInfo.rawMessage,
                                timestamp = roomInfo.time,
                                sourceName = roomInfo.sourceName,
                                avatarName = accountInfo.accountSummary.avatar,
                                userName = accountInfo.accountSummary.username,
                                onCopy = {},
                            )
                        }
                    }
                }
            }
        }
    }
}