package com.eynnzerr.bandoristation.feature.tutorial

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import bandoristationm.composeapp.generated.resources.Res
import com.eynnzerr.bandoristation.feature.home.HomeEffect
import com.eynnzerr.bandoristation.navigation.Screen
import com.eynnzerr.bandoristation.navigation.ext.navigateTo
import com.eynnzerr.bandoristation.ui.component.UserAvatar
import com.eynnzerr.bandoristation.ui.dialog.LoginDialog
import com.eynnzerr.bandoristation.ui.ext.appBarScroll
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TutorialScreen(
    navController: NavHostController,
) {
    val pagerState = rememberPagerState { 2 }
    val coroutineScope = rememberCoroutineScope()

    var showLoginDialog by remember { mutableStateOf(false) }
    var isLoggedIn by remember { mutableStateOf(false) }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxSize(),
        userScrollEnabled = false // controlled by buttons
    ) { page ->
        when (page) {
            0 -> IntroductionPage(onNext = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(1)
                }
            })
            1 -> LoginPage(
                isLoggedIn = isLoggedIn,
                onLoginClick = { showLoginDialog = true },
                onRegisterEncryptionClick = { /* TODO */ },
                onBack = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                },
                onFinish = {
                    navController.navigateTo(Screen.Home)
                }
            )
        }
    }

    LoginDialog(
        isVisible = showLoginDialog,
        onDismissRequest = { showLoginDialog = false },
        onEnterLogin = {
            isLoggedIn = true
            showLoginDialog = false
        }
        // currentScreen = loginDialogState,
        // onPopBack = { viewModel.sendEffect(AccountEffect.PopBackLoginDialog()) },
        // onDismissRequest = { viewModel.sendEffect(AccountEffect.ControlLoginDialog(false)) },
        // onHelp = { viewModel.sendEffect(AccountEffect.ControlLoginDialogScreen(LoginScreenState.HELP)) },
        // onEnterLogin = { viewModel.sendEffect(AccountEffect.ControlLoginDialogScreen(LoginScreenState.PASSWORD_LOGIN)) },
        //        onEnterToken = { viewModel.sendEffect(AccountEffect.ControlLoginDialogScreen(LoginScreenState.TOKEN_LOGIN)) },
        //        onEnterRegister = { viewModel.sendEffect(AccountEffect.ControlLoginDialogScreen(LoginScreenState.REGISTER)) },
        //        onEnterForgot = { viewModel.sendEffect(AccountEffect.ControlLoginDialogScreen(LoginScreenState.FORGOT_PASSWORD)) },
        //        onLoginWithToken = { token ->
        //            viewModel.sendEvent(GetUserInfo(token))
        //        },
        //        onLoginWithPassword = { username, password ->
        //            viewModel.sendEvent(Login(username, password))
        //        },
        //        onRegister = { username, password, email ->
        //            viewModel.sendEvent(Signup(username, password, email))
        //        },
        //        onSendVerificationCode = {
        //            viewModel.sendEvent(SendVerificationCode())
        //        },
        //        onVerifyCode = { code ->
        //            viewModel.sendEvent(VerifyEmail(code))
        //        },
        //        onSendCodeForResetPassword = {
        //            viewModel.sendEvent(ResetPasswordSendVCode(it))
        //        },
        //        onVerifyCodeForResetPassword = { email, code ->
        //            viewModel.sendEvent(ResetPasswordVerifyCode(email, code))
        //        },
        //        onResetPassword = {
        //            viewModel.sendEvent(ResetPassword(it))
        //        },
        //        sendCountDown = state.countDown
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntroductionPage(onNext: () -> Unit) {
    val richTextState = rememberRichTextState()
    var markdownContent by rememberSaveable { mutableStateOf("Loading...") }
    LaunchedEffect(Unit) {
        markdownContent = Res.readBytes("files/help.md").decodeToString()
        richTextState.setMarkdown(markdownContent)
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier.appBarScroll(true, scrollBehavior),
        topBar = {
            LargeTopAppBar(
                title = { Text("1. APP 介绍") },
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
                Text("下一步")
            }
        }
    ) { paddingValues ->
        RichText(
            state = richTextState,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(paddingValues = paddingValues)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(
    isLoggedIn: Boolean,
    onLoginClick: () -> Unit,
    onRegisterEncryptionClick: () -> Unit,
    onBack: () -> Unit,
    onFinish: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier.appBarScroll(true, scrollBehavior),
        topBar = {
            LargeTopAppBar(
                title = { Text("2. 账号设置") },
                navigationIcon = { },
                actions = {
                    IconButton(
                        onClick = {

                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                            contentDescription = "",
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
                    Text("上一步")
                }
                Button(onClick = onFinish) {
                    Text("完成")
                }
            }
        }
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
                avatarName = "",
                size = 128.dp
            )

            Text(
                "未登录",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)
            )

            if (isLoggedIn) {
                OutlinedButton(
                    onClick = onLoginClick,
                    modifier = Modifier.width(156.dp)
                ) {
                    Text("重新登录")
                }
            } else {
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier.width(156.dp)
                ) {
                    Text("登录 / 注册")
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onRegisterEncryptionClick,
                modifier = Modifier.width(156.dp),
                enabled = isLoggedIn,
            ) {
                Text("注册加密服务")
            }

            if (!isLoggedIn) {
                Text(
                    "*（需要登录）",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            }
        }
    }
}


@Preview
@Composable
fun TutorialScreenPreview() {
    TutorialScreen(rememberNavController())
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
        onLoginClick = {},
        onRegisterEncryptionClick = {},
        onBack = {},
        onFinish = {}
    )
}