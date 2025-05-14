package com.eynnzerr.bandoristation.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.eynnzerr.bandoristation.model.account.EditProfileInfo
import com.eynnzerr.bandoristation.ui.component.UserAvatar
import com.eynnzerr.bandoristation.ui.component.UserBannerImage
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDialog(
    isVisible: Boolean,
    avatar: String,
    profile: EditProfileInfo,
    onDismissRequest: () -> Unit,
) {
    if (isVisible) {
        BasicAlertDialog(
            modifier = Modifier.padding(vertical = 32.dp),
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            ),
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "账号资料",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    HorizontalDivider()

                    ProfileItem(
                        title = "头像",
                        content = {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.BottomEnd
                            ) {
                                UserAvatar(
                                    avatarName = avatar,
                                    size = 40.dp,
                                    onClick = {
                                        // 更换头像
                                    }
                                )
                            }
                        },
                        onClick = {
                            // 更换头像
                        }
                    )

                    ProfileItem(
                        title = "横幅背景",
                        content = {
                            UserBannerImage(
                                bannerName = profile.banner,
                                modifier = Modifier
                                    .height(40.dp)
                                    .width(100.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                            )
                        },
                        onClick = { /* 点击更换背景 */ }
                    )

                    ProfileItem(
                        title = "用户名",
                        content = {
                            Text(
                                text = profile.username,
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        onClick = { /* 点击编辑用户名 */ }
                    )

                    ProfileItem(
                        title = "个性签名",
                        content = {
                            Text(
                                text = profile.introduction,
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        onClick = { /* 点击编辑个性签名 */ }
                    )

                    ProfileItem(
                        title = "密码",
                        content = {
                            Text(
                                text = "********",
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        onClick = { /* 点击修改密码 */ }
                    )

                    ProfileItem(
                        title = "电子邮件",
                        content = {
                            Text(
                                text = profile.email,
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        onClick = { /* 点击编辑邮箱 */ }
                    )

                    ProfileItem(
                        title = "QQ",
                        content = {
                            Text(
                                text = profile.qq,
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        onClick = { /* 点击编辑QQ */ }
                    )

                    ProfileItem(
                        title = "国服游戏ID",
                        content = {
                            Text(
                                text = if (profile.playerId.cn.isEmpty()) "未绑定" else profile.playerId.cn.first().toString(),
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        onClick = { /* 点击编辑国服游戏ID */ }
                    )

                    ProfileItem(
                        title = "日服游戏ID",
                        content = {
                            Text(
                                text = if (profile.playerId.jp.isEmpty()) "未绑定" else profile.playerId.jp.first().toString(),
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        onClick = { /* 点击编辑日服游戏ID */ }
                    )

                    ProfileItem(
                        title = "主游戏账号",
                        content = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = profile.mainGameAccount.playerId.toString(),
                                    style = MaterialTheme.typography.bodyLarge,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        },
                        onClick = { /* 点击设置主游戏账号 */ }
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = onDismissRequest
                        ) {
                            Text("确定")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileItem(
    title: String,
    content: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier
                .width(100.dp)
                .padding(end = 16.dp)
        )

        content()
    }

    HorizontalDivider()
}

@Preview
@Composable
fun UserProfileDialogPreview() {
    MaterialTheme {
        EditProfileDialog(
            isVisible = true,
            onDismissRequest = {},
            avatar = "",
            profile = EditProfileInfo(),
        )
    }
}