package com.eynnzerr.bandoristation.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Token
import androidx.compose.material.icons.outlined.HighlightOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class LoginScreenState {
    INITIAL,
    HELP,
    PASSWORD_LOGIN,
    TOKEN_LOGIN,
    REGISTER,
    FORGOT_PASSWORD
}

@Composable
fun LoginDialog(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    onLoginWithPassword: (username: String, password: String) -> Unit = { _, _ -> },
    onLoginWithToken: (token: String) -> Unit = { _ -> },
    onRegister: (username: String, password: String, email: String) -> Unit = { _, _, _ -> },
    onSendVerificationCode: (email: String) -> Unit = { _ -> },
    onVerifyCode: (email: String, code: String) -> Unit = { _, _ -> },
) {
    if (isVisible) {
        var currentScreen by remember { mutableStateOf(LoginScreenState.INITIAL) }

        // Form fields
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var token by remember { mutableStateOf("") }
        var verificationCode by remember { mutableStateOf("") }

        Dialog(onDismissRequest = onDismissRequest) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    // Dialog header with title and back button if not on initial screen
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (currentScreen != LoginScreenState.INITIAL) {
                                IconButton(onClick = {
                                    currentScreen = when (currentScreen) {
                                        LoginScreenState.REGISTER, LoginScreenState.FORGOT_PASSWORD -> LoginScreenState.PASSWORD_LOGIN
                                        else -> LoginScreenState.INITIAL
                                    }
                                }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                }
                            }
                            Text(
                                text = when (currentScreen) {
                                    LoginScreenState.INITIAL -> "登录选项"
                                    LoginScreenState.PASSWORD_LOGIN -> "密码登录"
                                    LoginScreenState.TOKEN_LOGIN -> "Token登录"
                                    LoginScreenState.REGISTER -> "注册账号"
                                    LoginScreenState.FORGOT_PASSWORD -> "找回密码"
                                    LoginScreenState.HELP -> "帮助"
                                },
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        if (currentScreen == LoginScreenState.INITIAL) {
                            Row {
                                IconButton(onClick = {
                                    currentScreen = LoginScreenState.HELP
                                }) {
                                    Icon(Icons.AutoMirrored.Outlined.Help, contentDescription = "Help")
                                }
                                IconButton(
                                    onClick = onDismissRequest
                                ) {
                                    Icon(
                                        Icons.Outlined.HighlightOff,
                                        contentDescription = "Exit",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Content based on current screen
                    when (currentScreen) {
                        LoginScreenState.INITIAL -> {
                            // Initial screen with login options
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Button(
                                    onClick = { currentScreen = LoginScreenState.PASSWORD_LOGIN },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Password,
                                        contentDescription = null,
                                        modifier = Modifier.size(ButtonDefaults.IconSize)
                                    )
                                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                    Text("密码登录")
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedButton(
                                    onClick = { currentScreen = LoginScreenState.TOKEN_LOGIN },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Token,
                                        contentDescription = null,
                                        modifier = Modifier.size(ButtonDefaults.IconSize)
                                    )
                                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                    Text("使用Token")
                                }
                            }
                        }

                        LoginScreenState.PASSWORD_LOGIN -> {
                            // Password login form
                            Column(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = username,
                                    onValueChange = { username = it },
                                    label = { Text("用户名") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    label = { Text("密码") },
                                    visualTransformation = PasswordVisualTransformation(),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { onLoginWithPassword(username, password) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("登录")
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    TextButton(onClick = { currentScreen = LoginScreenState.REGISTER }) {
                                        Text("注册账号")
                                    }

                                    TextButton(onClick = { currentScreen = LoginScreenState.FORGOT_PASSWORD }) {
                                        Text("忘记密码")
                                    }
                                }
                            }
                        }

                        LoginScreenState.TOKEN_LOGIN -> {
                            // Token login form
                            Column(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = token,
                                    onValueChange = { token = it },
                                    label = { Text("Token") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { onLoginWithToken(token) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("登录")
                                }
                            }
                        }

                        LoginScreenState.REGISTER -> {
                            // Registration form
                            Column(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = username,
                                    onValueChange = { username = it },
                                    label = { Text("用户名") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    label = { Text("密码") },
                                    visualTransformation = PasswordVisualTransformation(),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = confirmPassword,
                                    onValueChange = { confirmPassword = it },
                                    label = { Text("再次输入密码") },
                                    visualTransformation = PasswordVisualTransformation(),
                                    modifier = Modifier.fillMaxWidth(),
                                    isError = password != confirmPassword && confirmPassword.isNotEmpty()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text("邮箱地址") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { onRegister(username, password, email) },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = password == confirmPassword &&
                                            password.isNotEmpty() &&
                                            username.isNotEmpty() &&
                                            email.isNotEmpty()
                                ) {
                                    Text("注册")
                                }
                            }
                        }

                        LoginScreenState.FORGOT_PASSWORD -> {
                            // Forgot password form
                            Column(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text("邮箱地址") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    OutlinedTextField(
                                        value = verificationCode,
                                        onValueChange = { verificationCode = it },
                                        label = { Text("验证码") },
                                        modifier = Modifier.weight(1f)
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Button(
                                        onClick = { onSendVerificationCode(email) },
                                        enabled = email.isNotEmpty(),
                                        shape = MaterialTheme.shapes.medium
                                    ) {
                                        Text("发送验证码")
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { onVerifyCode(email, verificationCode) },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = email.isNotEmpty() && verificationCode.isNotEmpty()
                                ) {
                                    Text("下一步")
                                }
                            }
                        }

                        LoginScreenState.HELP -> {
                            Text("帮助文本")
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun LoginDialogPreview() {
    LoginDialog(
        isVisible = true,
        onDismissRequest = {},
    )
}