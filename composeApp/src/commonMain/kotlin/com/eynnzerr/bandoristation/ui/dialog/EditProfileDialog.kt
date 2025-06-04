package com.eynnzerr.bandoristation.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.eynnzerr.bandoristation.model.account.EditProfileInfo
import com.eynnzerr.bandoristation.ui.component.UserAvatar
import com.eynnzerr.bandoristation.ui.component.UserBannerImage
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDialog(
    isVisible: Boolean,
    avatar: String,
    profile: EditProfileInfo,
    onDismissRequest: () -> Unit,
    onAvatarClick: () -> Unit = {},
    onBannerClick: () -> Unit = {},
    onUsernameEdit: (String) -> Unit = {},
    onIntroductionEdit: (String) -> Unit = {},
    onPasswordEdit: (oldPassword: String, newPassword: String) -> Unit = { _, _ -> },
    onEmailEdit: (newEmail: String, verificationCode: String) -> Unit = { _, _ -> },
    onQQEdit: (Long) -> Unit = {},
    onSendEmailVerification: (String) -> Unit = {},
) {
    var showUsernameDialog by remember { mutableStateOf(false) }
    var showIntroductionDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showEmailDialog by remember { mutableStateOf(false) }
    var showQQDialog by remember { mutableStateOf(false) }

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
                                    onClick = onAvatarClick
                                )
                            }
                        },
                        onClick = onAvatarClick
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
                        onClick = onBannerClick
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
                        onClick = { showUsernameDialog = true }
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
                        onClick = { showIntroductionDialog = true }
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
                        onClick = { showPasswordDialog = true }
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
                        onClick = { showEmailDialog = true }
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
                        onClick = { showQQDialog = true }
                    )

//                    ProfileItem(
//                        title = "国服游戏ID",
//                        content = {
//                            Text(
//                                text = if (profile.playerId.cn.isEmpty()) "未绑定" else profile.playerId.cn.first().toString(),
//                                style = MaterialTheme.typography.bodyLarge,
//                                maxLines = 1,
//                                overflow = TextOverflow.Ellipsis
//                            )
//                        },
//                        onClick = { /* 点击编辑国服游戏ID */ }
//                    )

//                    ProfileItem(
//                        title = "日服游戏ID",
//                        content = {
//                            Text(
//                                text = if (profile.playerId.jp.isEmpty()) "未绑定" else profile.playerId.jp.first().toString(),
//                                style = MaterialTheme.typography.bodyLarge,
//                                maxLines = 1,
//                                overflow = TextOverflow.Ellipsis
//                            )
//                        },
//                        onClick = { /* 点击编辑日服游戏ID */ }
//                    )

//                    ProfileItem(
//                        title = "主游戏账号",
//                        content = {
//                            Row(
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Text(
//                                    text = profile.mainGameAccount.playerId.toString(),
//                                    style = MaterialTheme.typography.bodyLarge,
//                                    maxLines = 1,
//                                    overflow = TextOverflow.Ellipsis
//                                )
//                            }
//                        },
//                        onClick = { /* 点击设置主游戏账号 */ }
//                    )

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

    // 各种编辑对话框
    if (showUsernameDialog) {
        EditUsernameDialog(
            currentValue = profile.username,
            onDismiss = { showUsernameDialog = false },
            onConfirm = { newUsername ->
                onUsernameEdit(newUsername)
                showUsernameDialog = false
            }
        )
    }

    if (showIntroductionDialog) {
        EditIntroductionDialog(
            currentValue = profile.introduction,
            onDismiss = { showIntroductionDialog = false },
            onConfirm = { newIntroduction ->
                onIntroductionEdit(newIntroduction)
                showIntroductionDialog = false
            }
        )
    }

    if (showPasswordDialog) {
        EditPasswordDialog(
            onDismiss = { showPasswordDialog = false },
            onConfirm = { oldPassword, newPassword ->
                onPasswordEdit(oldPassword, newPassword)
                showPasswordDialog = false
            }
        )
    }

    if (showEmailDialog) {
        EditEmailDialog(
            currentEmail = profile.email,
            onDismiss = { showEmailDialog = false },
            onConfirm = { newEmail, verificationCode ->
                onEmailEdit(newEmail, verificationCode)
                showEmailDialog = false
            },
            onSendVerification = onSendEmailVerification
        )
    }

    if (showQQDialog) {
        EditQQDialog(
            currentValue = profile.qq,
            onDismiss = { showQQDialog = false },
            onConfirm = { newQQ ->
                onQQEdit(newQQ.toLongOrNull() ?: 0)
                showQQDialog = false
            }
        )
    }
}

// 编辑用户名对话框
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUsernameDialog(
    currentValue: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var textValue by remember { mutableStateOf(currentValue) }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "编辑用户名",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    singleLine = true,
                    label = { Text("请输入用户名") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (textValue.isNotBlank() && textValue != currentValue) {
                                onConfirm(textValue)
                            }
                        }
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("取消")
                    }

                    Button(
                        onClick = {
                            if (textValue.isNotBlank() && textValue != currentValue) {
                                onConfirm(textValue)
                            }
                        },
                        enabled = textValue.isNotBlank() && textValue != currentValue
                    ) {
                        Text("确定")
                    }
                }
            }
        }
    }
}

// 编辑个性签名对话框
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditIntroductionDialog(
    currentValue: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var textValue by remember { mutableStateOf(currentValue) }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "编辑个性签名",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    maxLines = 3,
                    label = { Text("请输入个性签名") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("取消")
                    }

                    Button(
                        onClick = {
                            if (textValue != currentValue) {
                                onConfirm(textValue)
                            }
                        },
                        enabled = textValue != currentValue
                    ) {
                        Text("确定")
                    }
                }
            }
        }
    }
}

// 修改密码对话框
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (oldPassword: String, newPassword: String) -> Unit
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val isValid = oldPassword.isNotBlank() &&
            newPassword.isNotBlank() &&
            confirmPassword.isNotBlank() &&
            newPassword == confirmPassword &&
            newPassword.length >= 6

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "修改密码",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 原密码
                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    label = { Text("原密码") },
                    singleLine = true,
                    visualTransformation = if (oldPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(onClick = { oldPasswordVisible = !oldPasswordVisible }) {
                            Icon(
                                imageVector = if (oldPasswordVisible) {
                                    Icons.Filled.Visibility
                                } else {
                                    Icons.Filled.VisibilityOff
                                },
                                contentDescription = if (oldPasswordVisible) "隐藏密码" else "显示密码"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                // 新密码
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    label = { Text("新密码") },
                    singleLine = true,
                    visualTransformation = if (newPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                            Icon(
                                imageVector = if (newPasswordVisible) {
                                    Icons.Filled.Visibility
                                } else {
                                    Icons.Filled.VisibilityOff
                                },
                                contentDescription = if (newPasswordVisible) "隐藏密码" else "显示密码"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    supportingText = {
                        Text(
                            text = "密码长度至少6位",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )

                // 确认新密码
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    label = { Text("再次输入新密码") },
                    singleLine = true,
                    visualTransformation = if (confirmPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) {
                                    Icons.Filled.Visibility
                                } else {
                                    Icons.Filled.VisibilityOff
                                },
                                contentDescription = if (confirmPasswordVisible) "隐藏密码" else "显示密码"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = confirmPassword.isNotBlank() && newPassword != confirmPassword,
                    supportingText = if (confirmPassword.isNotBlank() && newPassword != confirmPassword) {
                        {
                            Text(
                                text = "两次输入的密码不一致",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    } else null
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("取消")
                    }

                    Button(
                        onClick = {
                            if (isValid) {
                                onConfirm(oldPassword, newPassword)
                            }
                        },
                        enabled = isValid
                    ) {
                        Text("确定")
                    }
                }
            }
        }
    }
}

// 编辑邮箱对话框
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEmailDialog(
    currentEmail: String,
    onDismiss: () -> Unit,
    onConfirm: (newEmail: String, verificationCode: String) -> Unit,
    onSendVerification: (String) -> Unit
) {
    var newEmail by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf("") }
    var countdown by remember { mutableStateOf(0) }
    var isCodeSent by remember { mutableStateOf(false) }

    // 倒计时效果
    LaunchedEffect(countdown) {
        if (countdown > 0) {
            delay(1000)
            countdown--
        }
    }

    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return email.matches(emailRegex)
    }

    val isEmailValid = isValidEmail(newEmail)
    val canSendCode = isEmailValid && newEmail != currentEmail && countdown == 0
    val canConfirm = isEmailValid && verificationCode.isNotBlank() && isCodeSent

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "修改邮箱",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 当前邮箱提示
                Text(
                    text = "当前邮箱：$currentEmail",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 新邮箱输入
                OutlinedTextField(
                    value = newEmail,
                    onValueChange = {
                        newEmail = it
                        isCodeSent = false
                        countdown = 0
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    label = { Text("新邮箱地址") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    isError = newEmail.isNotBlank() && !isEmailValid,
                    supportingText = if (newEmail.isNotBlank() && !isEmailValid) {
                        {
                            Text(
                                text = "请输入有效的邮箱地址",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    } else null
                )

                // 验证码输入和发送按钮
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = verificationCode,
                        onValueChange = { verificationCode = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("验证码") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        )
                    )

                    Button(
                        onClick = {
                            onSendVerification(newEmail)
                            isCodeSent = true
                            countdown = 60
                        },
                        enabled = canSendCode,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = if (countdown > 0) "${countdown}s" else "发送验证码"
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("取消")
                    }

                    Button(
                        onClick = {
                            if (canConfirm) {
                                onConfirm(newEmail, verificationCode)
                            }
                        },
                        enabled = canConfirm
                    ) {
                        Text("确定")
                    }
                }
            }
        }
    }
}

// 编辑QQ对话框
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditQQDialog(
    currentValue: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var textValue by remember { mutableStateOf(currentValue) }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "编辑QQ",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = textValue,
                    onValueChange = {
                        // 只允许输入数字
                        if (it.all { char -> char.isDigit() }) {
                            textValue = it
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    label = { Text("QQ号码") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (textValue.isNotBlank() && textValue != currentValue) {
                                onConfirm(textValue)
                            }
                        }
                    ),
                    supportingText = {
                        Text(
                            text = "请输入5-11位QQ号码",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("取消")
                    }

                    Button(
                        onClick = {
                            if (textValue.isNotBlank() && textValue != currentValue) {
                                onConfirm(textValue)
                            }
                        },
                        enabled = textValue.isNotBlank() &&
                                textValue != currentValue &&
                                textValue.length in 5..11
                    ) {
                        Text("确定")
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
            .padding(vertical = 18.dp),
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