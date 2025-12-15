package com.eynnzerr.bandoristation.feature.tutorial

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.loading
import bandoristationm.composeapp.generated.resources.tutorial_account_settings_title
import bandoristationm.composeapp.generated.resources.tutorial_encryption_server
import bandoristationm.composeapp.generated.resources.tutorial_finish
import bandoristationm.composeapp.generated.resources.tutorial_help_icon_desc
import bandoristationm.composeapp.generated.resources.tutorial_introduction_title
import bandoristationm.composeapp.generated.resources.tutorial_login_register
import bandoristationm.composeapp.generated.resources.tutorial_login_required
import bandoristationm.composeapp.generated.resources.tutorial_logout
import bandoristationm.composeapp.generated.resources.tutorial_next_step
import bandoristationm.composeapp.generated.resources.tutorial_not_logged_in
import bandoristationm.composeapp.generated.resources.tutorial_previous_step
import bandoristationm.composeapp.generated.resources.tutorial_register_encryption
import bandoristationm.composeapp.generated.resources.tutorial_server_connected
import bandoristationm.composeapp.generated.resources.tutorial_server_disconnected
import bandoristationm.composeapp.generated.resources.tutorial_station_server
import com.eynnzerr.bandoristation.navigation.Screen
import com.eynnzerr.bandoristation.navigation.ext.navigateTo
import com.eynnzerr.bandoristation.ui.component.UserAvatar
import com.eynnzerr.bandoristation.ui.dialog.HelpDialog
import com.eynnzerr.bandoristation.ui.dialog.LoginDialog
import com.eynnzerr.bandoristation.ui.dialog.LoginScreenState
import com.eynnzerr.bandoristation.ui.ext.appBarScroll
import com.eynnzerr.bandoristation.utils.rememberFlowWithLifecycle
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TutorialScreen(
    navController: NavHostController,
    viewModel: TutorialViewModel = koinViewModel<TutorialViewModel>()
) {
    val pagerState = rememberPagerState { 2 }
    val coroutineScope = rememberCoroutineScope()

    val state by viewModel.state.collectAsStateWithLifecycle()
    val effect = rememberFlowWithLifecycle(viewModel.effect)
    var showLoginDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var loginDialogState by remember { mutableStateOf(LoginScreenState.INITIAL) }
    var snackbarText by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(effect) {
        effect.collect { action ->
            when (action) {
                is TutorialEffect.ControlLoginDialog -> {
                    showLoginDialog = action.visible
                    loginDialogState = LoginScreenState.INITIAL
                }
                is TutorialEffect.ControlLoginDialogScreen -> {
                    loginDialogState = action.destination
                }
                is TutorialEffect.NavigateToHome -> {
                    navController.navigateTo(Screen.Home, true)
                }
                is TutorialEffect.PopBackLoginDialog -> {
                    loginDialogState = when (loginDialogState) {
                        LoginScreenState.REGISTER, LoginScreenState.FORGOT_PASSWORD -> LoginScreenState.PASSWORD_LOGIN
                        LoginScreenState.RESET_PASSWORD -> LoginScreenState.FORGOT_PASSWORD
                        LoginScreenState.VERIFY_EMAIL -> LoginScreenState.REGISTER
                        else -> LoginScreenState.INITIAL
                    }
                }
                is TutorialEffect.ShowSnackbar -> {
                    snackbarText = action.text
                }
                is TutorialEffect.ControlHelpDialog -> {
                    showHelpDialog = action.visible
                }
            }
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxSize(),
        userScrollEnabled = false
    ) { page ->
        when (page) {
            0 -> IntroductionPage(onNext = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(1)
                }
            })
            1 -> LoginPage(
                isLoggedIn = state.isLoggedIn,
                userAvatar = state.avatar,
                userName = state.username,
                isStationConnected = state.isStationConnected,
                isEncryptionConnected = state.isEncryptionConnected,
                snackbarMessage = snackbarText,
                onSnackbarShown = { snackbarText = null },
                onLoginClick = {
                    if (state.isLoggedIn) {
                        // 退出登录
                        viewModel.sendEvent(TutorialIntent.Logout())
                    } else {
                        // 打开登录对话框
                        viewModel.sendEffect(TutorialEffect.ControlLoginDialog(true))
                    }
                },
                onClickHelp = {
                    viewModel.sendEffect(TutorialEffect.ControlHelpDialog(true))
                },
                onRegisterEncryptionClick = {
                    viewModel.sendEvent(TutorialIntent.RegisterEncryption())
                },
                onBack = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                },
                onFinish = {
                    viewModel.sendEffect(TutorialEffect.NavigateToHome())
                }
            )
        }
    }

    LoginDialog(
        isVisible = showLoginDialog,
        currentScreen = loginDialogState,
        onPopBack = { viewModel.sendEffect(TutorialEffect.PopBackLoginDialog()) },
        onDismissRequest = { viewModel.sendEffect(TutorialEffect.ControlLoginDialog(false)) },
        onHelp = { viewModel.sendEffect(TutorialEffect.ControlLoginDialogScreen(LoginScreenState.HELP)) },
        onEnterLogin = { viewModel.sendEffect(TutorialEffect.ControlLoginDialogScreen(LoginScreenState.PASSWORD_LOGIN)) },
        onEnterToken = { viewModel.sendEffect(TutorialEffect.ControlLoginDialogScreen(LoginScreenState.TOKEN_LOGIN)) },
        onEnterRegister = { viewModel.sendEffect(TutorialEffect.ControlLoginDialogScreen(LoginScreenState.REGISTER)) },
        onEnterForgot = { viewModel.sendEffect(TutorialEffect.ControlLoginDialogScreen(LoginScreenState.FORGOT_PASSWORD)) },
        onLoginWithToken = { token ->
            viewModel.sendEvent(TutorialIntent.GetUserInfo(token))
        },
        onLoginWithPassword = { username, password ->
            viewModel.sendEvent(TutorialIntent.Login(username, password))
        },
        onRegister = { username, password, email ->
            viewModel.sendEvent(TutorialIntent.Signup(username, password, email))
        },
        onSendVerificationCode = {
            viewModel.sendEvent(TutorialIntent.SendVerificationCode())
        },
        onVerifyCode = { code ->
            viewModel.sendEvent(TutorialIntent.VerifyEmail(code))
        },
        onSendCodeForResetPassword = {
            viewModel.sendEvent(TutorialIntent.ResetPasswordSendVCode(it))
        },
        onVerifyCodeForResetPassword = { email, code ->
            viewModel.sendEvent(TutorialIntent.ResetPasswordVerifyCode(email, code))
        },
        onResetPassword = {
            viewModel.sendEvent(TutorialIntent.ResetPassword(it))
        },
        sendCountDown = state.countDown
    )

    HelpDialog(
        isVisible = showHelpDialog,
        markdownPath = "files/guidance.md",
        onDismissRequest = { viewModel.sendEffect(TutorialEffect.ControlHelpDialog(false)) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntroductionPage(onNext: () -> Unit) {
    val richTextState = rememberRichTextState()
    var markdownContent by rememberSaveable { mutableStateOf("Loading ...") }
    LaunchedEffect(Unit) {
        markdownContent = Res.readBytes("files/help.md").decodeToString()
        richTextState.setMarkdown(markdownContent)
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier.appBarScroll(true, scrollBehavior),
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(Res.string.tutorial_introduction_title)) },
                navigationIcon = { },
                actions = { },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(stringResource(Res.string.tutorial_next_step))
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = paddingValues)
        ) {
            RichText(
                state = richTextState,
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(
    isLoggedIn: Boolean,
    userAvatar: String,
    userName: String,
    isStationConnected: Boolean,
    isEncryptionConnected: Boolean,
    snackbarMessage: String?,
    onSnackbarShown: () -> Unit,
    onClickHelp: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterEncryptionClick: () -> Unit,
    onBack: () -> Unit,
    onFinish: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        if (snackbarMessage != null) {
            snackbarHostState.showSnackbar(
                message = snackbarMessage,
                duration = SnackbarDuration.Short
            )
            onSnackbarShown()
        }
    }

    Scaffold(
        modifier = Modifier.appBarScroll(true, scrollBehavior),
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(Res.string.tutorial_account_settings_title)) },
                navigationIcon = { },
                actions = {
                    IconButton(
                        onClick = onClickHelp
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                            contentDescription = stringResource(Res.string.tutorial_help_icon_desc),
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = onBack) {
                    Text(stringResource(Res.string.tutorial_previous_step))
                }
                Button(onClick = onFinish) {
                    Text(stringResource(Res.string.tutorial_finish))
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            UserAvatar(
                avatarName = userAvatar,
                size = 108.dp
            )

            Text(
                userName,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)
            )

            if (isLoggedIn) {
                OutlinedButton(
                    onClick = onLoginClick,
                    modifier = Modifier.width(156.dp)
                ) {
                    Text(stringResource(Res.string.tutorial_logout), color = MaterialTheme.colorScheme.error)
                }
            } else {
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier.width(156.dp)
                ) {
                    Text(stringResource(Res.string.tutorial_login_register))
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onRegisterEncryptionClick,
                modifier = Modifier.width(156.dp),
                enabled = isLoggedIn,
            ) {
                Text(stringResource(Res.string.tutorial_register_encryption))
            }

            if (!isLoggedIn) {
                Text(
                    stringResource(Res.string.tutorial_login_required),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            }

            Spacer(Modifier.height(32.dp))

            ConnectionStatus(
                name = stringResource(Res.string.tutorial_station_server),
                isConnected = isStationConnected
            )
            Spacer(Modifier.height(8.dp))
            ConnectionStatus(
                name = stringResource(Res.string.tutorial_encryption_server),
                isConnected = isEncryptionConnected
            )
        }
    }
}

@Composable
private fun ConnectionStatus(name: String, isConnected: Boolean) {
    val icon = if (isConnected) Icons.Filled.CheckCircle else Icons.Filled.Warning
    val color = if (isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = if (isConnected) stringResource(Res.string.tutorial_server_connected, name) else stringResource(Res.string.tutorial_server_disconnected, name),
            style = MaterialTheme.typography.bodyMedium,
            color = color
        )
    }
}

@Preview
@Composable
fun IntroductionPagePreview() {
    IntroductionPage(onNext = {})
}

@Preview
@Composable
fun LoginPagePreview() {
    LoginPage(
        isLoggedIn = false,
        userAvatar = "",
        userName = stringResource(Res.string.tutorial_not_logged_in),
        isStationConnected = false,
        isEncryptionConnected = false,
        onLoginClick = {},
        onClickHelp = {},
        onRegisterEncryptionClick = {},
        onBack = {},
        onFinish = {},
        onSnackbarShown = {},
        snackbarMessage = null,
    )
}