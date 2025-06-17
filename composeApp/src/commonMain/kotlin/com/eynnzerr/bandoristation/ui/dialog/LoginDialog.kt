package com.eynnzerr.bandoristation.ui.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
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
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.edit_email_resend_countdown
import bandoristationm.composeapp.generated.resources.login_dialog_back_arrow_desc
import bandoristationm.composeapp.generated.resources.login_dialog_button_change_email
import bandoristationm.composeapp.generated.resources.login_dialog_button_forgot_password_link
import bandoristationm.composeapp.generated.resources.login_dialog_button_login_action
import bandoristationm.composeapp.generated.resources.login_dialog_button_next
import bandoristationm.composeapp.generated.resources.login_dialog_button_password_login_option
import bandoristationm.composeapp.generated.resources.login_dialog_button_register_action
import bandoristationm.composeapp.generated.resources.login_dialog_button_register_link
import bandoristationm.composeapp.generated.resources.login_dialog_button_token_login_option
import bandoristationm.composeapp.generated.resources.login_dialog_button_verify_action
import bandoristationm.composeapp.generated.resources.login_dialog_exit_icon_desc
import bandoristationm.composeapp.generated.resources.login_dialog_label_code
import bandoristationm.composeapp.generated.resources.login_dialog_label_confirm_password_register
import bandoristationm.composeapp.generated.resources.login_dialog_label_email_address
import bandoristationm.composeapp.generated.resources.login_dialog_label_token
import bandoristationm.composeapp.generated.resources.login_dialog_title_reset_password
import bandoristationm.composeapp.generated.resources.login_dialog_button_reset_action
import bandoristationm.composeapp.generated.resources.login_dialog_title_forgot_password
import bandoristationm.composeapp.generated.resources.login_dialog_title_options
import bandoristationm.composeapp.generated.resources.login_dialog_title_password_login
import bandoristationm.composeapp.generated.resources.login_dialog_title_register
import bandoristationm.composeapp.generated.resources.login_dialog_title_token_login
import bandoristationm.composeapp.generated.resources.login_dialog_title_verify_email
import bandoristationm.composeapp.generated.resources.login_help
import bandoristationm.composeapp.generated.resources.edit_profile_password
import bandoristationm.composeapp.generated.resources.edit_profile_username
import bandoristationm.composeapp.generated.resources.edit_password_new_password
import bandoristationm.composeapp.generated.resources.edit_email_send_verification_code
import bandoristationm.composeapp.generated.resources.help_dialog_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class LoginScreenState {
    INITIAL,
    HELP,
    PASSWORD_LOGIN,
    TOKEN_LOGIN,
    REGISTER,
    FORGOT_PASSWORD,
    RESET_PASSWORD,
    VERIFY_EMAIL,
}

@Composable
fun LoginDialog(
    isVisible: Boolean,
    currentScreen: LoginScreenState = LoginScreenState.INITIAL,
    onPopBack: () -> Unit = {},
    onDismissRequest: () -> Unit = {},
    onHelp: () -> Unit = {},
    onEnterLogin: () -> Unit = {},
    onEnterToken: () -> Unit = {},
    onEnterRegister: () -> Unit = {},
    onEnterForgot: () -> Unit = {},
    onLoginWithPassword: (username: String, password: String) -> Unit = { _, _ -> },
    onLoginWithToken: (token: String) -> Unit = { _ -> },
    onRegister: (username: String, password: String, email: String) -> Unit = { _, _, _ -> },
    onSendVerificationCode: () -> Unit = {},
    onVerifyCode: (code: String) -> Unit = { _ -> },
    onSendCodeForResetPassword: (email: String) -> Unit = { _ -> },
    onVerifyCodeForResetPassword: (code: String) -> Unit = { _ -> },
    onResetPassword: (password: String) -> Unit = { _ -> },
    sendCountDown: Int = 0,
) {
    if (isVisible) {
        // var currentScreen by remember { mutableStateOf(LoginScreenState.INITIAL) }

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
                                IconButton(onClick = onPopBack) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.login_dialog_back_arrow_desc))
                                }
                            }
                            Text(
                                text = when (currentScreen) {
                                    LoginScreenState.INITIAL -> stringResource(Res.string.login_dialog_title_options)
                                    LoginScreenState.PASSWORD_LOGIN -> stringResource(Res.string.login_dialog_title_password_login)
                                    LoginScreenState.TOKEN_LOGIN -> stringResource(Res.string.login_dialog_title_token_login)
                                    LoginScreenState.REGISTER -> stringResource(Res.string.login_dialog_title_register)
                                    LoginScreenState.FORGOT_PASSWORD -> stringResource(Res.string.login_dialog_title_forgot_password)
                                    LoginScreenState.RESET_PASSWORD -> stringResource(Res.string.login_dialog_title_reset_password)
                                    LoginScreenState.HELP -> stringResource(Res.string.help_dialog_title)
                                    LoginScreenState.VERIFY_EMAIL -> stringResource(Res.string.login_dialog_title_verify_email)
                                },
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        if (currentScreen == LoginScreenState.INITIAL) {
                            Row {
                                IconButton(onClick = onHelp) {
                                    Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = stringResource(Res.string.help_dialog_title))
                                }
                                IconButton(
                                    onClick = onDismissRequest
                                ) {
                                    Icon(
                                        Icons.Outlined.HighlightOff,
                                        contentDescription = stringResource(Res.string.login_dialog_exit_icon_desc),
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
                                    onClick = onEnterLogin,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Password,
                                        contentDescription = null,
                                        modifier = Modifier.size(ButtonDefaults.IconSize)
                                    )
                                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                    Text(stringResource(Res.string.login_dialog_button_password_login_option))
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedButton(
                                    onClick = onEnterToken,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Token,
                                        contentDescription = null,
                                        modifier = Modifier.size(ButtonDefaults.IconSize)
                                    )
                                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                    Text(stringResource(Res.string.login_dialog_button_token_login_option))
                                }
                            }
                        }

                        LoginScreenState.PASSWORD_LOGIN -> {
                            // Password login form
                            Column(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = username,
                                    onValueChange = { username = it },
                                    label = { Text(stringResource(Res.string.edit_profile_username)) },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    label = { Text(stringResource(Res.string.edit_profile_password)) },
                                    visualTransformation = PasswordVisualTransformation(),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { onLoginWithPassword(username, password) },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = username.isNotBlank() && password.isNotBlank()
                                ) {
                                    Text(stringResource(Res.string.login_dialog_button_login_action))
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    TextButton(onClick = onEnterRegister) {
                                        Text(stringResource(Res.string.login_dialog_button_register_link))
                                    }

                                    TextButton(onClick = onEnterForgot) {
                                        Text(stringResource(Res.string.login_dialog_button_forgot_password_link))
                                    }
                                }
                            }
                        }

                        LoginScreenState.TOKEN_LOGIN -> {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = token,
                                    onValueChange = { token = it },
                                    label = { Text(stringResource(Res.string.login_dialog_label_token)) },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { onLoginWithToken(token) },
                                    enabled = token.isNotBlank(),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(stringResource(Res.string.login_dialog_button_login_action))
                                }
                            }
                        }

                        LoginScreenState.REGISTER -> {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = username,
                                    onValueChange = { username = it },
                                    label = { Text(stringResource(Res.string.edit_profile_username)) },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    label = { Text(stringResource(Res.string.edit_profile_password)) },
                                    visualTransformation = PasswordVisualTransformation(),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = confirmPassword,
                                    onValueChange = { confirmPassword = it },
                                    label = { Text(stringResource(Res.string.login_dialog_label_confirm_password_register)) },
                                    visualTransformation = PasswordVisualTransformation(),
                                    modifier = Modifier.fillMaxWidth(),
                                    isError = password != confirmPassword && confirmPassword.isNotEmpty()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text(stringResource(Res.string.login_dialog_label_email_address)) },
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
                                    Text(stringResource(Res.string.login_dialog_button_register_action))
                                }
                            }
                        }

                        LoginScreenState.FORGOT_PASSWORD -> {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text(stringResource(Res.string.login_dialog_label_email_address)) },
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
                                        label = { Text(stringResource(Res.string.login_dialog_label_code)) },
                                        modifier = Modifier.weight(1f)
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Button(
                                        onClick = { onSendCodeForResetPassword(email) },
                                        shape = MaterialTheme.shapes.medium,
                                        enabled = email.isNotEmpty() && sendCountDown <= 0
                                    ) {
                                        Text(text = if (sendCountDown <= 0) stringResource(Res.string.edit_email_send_verification_code) else stringResource(Res.string.edit_email_resend_countdown, sendCountDown))
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        onVerifyCodeForResetPassword(verificationCode)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = email.isNotEmpty() && verificationCode.isNotEmpty()
                                ) {
                                    Text(stringResource(Res.string.login_dialog_button_next))
                                }
                            }
                        }

                        LoginScreenState.RESET_PASSWORD -> {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    label = { Text(stringResource(Res.string.edit_password_new_password)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    visualTransformation = PasswordVisualTransformation(),
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { onResetPassword(password) },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = password.isNotEmpty(),
                                ) {
                                    Text(stringResource(Res.string.login_dialog_button_reset_action))
                                }
                            }
                        }

                        LoginScreenState.HELP -> {
                            Text(stringResource(Res.string.login_help))
                        }

                        LoginScreenState.VERIFY_EMAIL -> {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        // TODO 修改邮箱地址
                                    },
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Text(stringResource(Res.string.login_dialog_button_change_email))
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    OutlinedTextField(
                                        value = verificationCode,
                                        onValueChange = { verificationCode = it },
                                        label = { Text(stringResource(Res.string.login_dialog_label_code)) },
                                        modifier = Modifier.weight(1f)
                                    )

                                    Button(
                                        modifier = Modifier.padding(start = 12.dp),
                                        onClick = onSendVerificationCode,
                                        shape = MaterialTheme.shapes.medium,
                                        enabled = sendCountDown <= 0
                                    ) {
                                        Text(text = if (sendCountDown <= 0) stringResource(Res.string.edit_email_send_verification_code) else stringResource(Res.string.edit_email_resend_countdown, sendCountDown))
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { onVerifyCode(verificationCode) },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = verificationCode.isNotEmpty()
                                ) {
                                    Text(stringResource(Res.string.login_dialog_button_verify_action))
                                }
                            }
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
        onPopBack = {},
    )
}
