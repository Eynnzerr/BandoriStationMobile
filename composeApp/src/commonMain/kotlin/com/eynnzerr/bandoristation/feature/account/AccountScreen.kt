package com.eynnzerr.bandoristation.feature.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Token
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.copy_room_snackbar
import com.eynnzerr.bandoristation.feature.account.AccountIntent.*
import com.eynnzerr.bandoristation.navigation.Screen
import com.eynnzerr.bandoristation.navigation.ext.navigateTo
import com.eynnzerr.bandoristation.ui.common.LocalAppProperty
import com.eynnzerr.bandoristation.ui.component.EditAccountButton
import com.eynnzerr.bandoristation.ui.dialog.LoginDialog
import com.eynnzerr.bandoristation.ui.component.app.SuiteScaffold
import com.eynnzerr.bandoristation.ui.component.UserProfile
import com.eynnzerr.bandoristation.ui.dialog.EditProfileDialog
import com.eynnzerr.bandoristation.ui.dialog.FollowListDialog
import com.eynnzerr.bandoristation.ui.dialog.LoginScreenState
import com.eynnzerr.bandoristation.utils.rememberFlowWithLifecycle
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    navController: NavHostController,
    viewModel: AccountViewModel = koinViewModel<AccountViewModel>()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val isExpanded = LocalAppProperty.current.screenInfo.isLandscape()

    val state by viewModel.state.collectAsStateWithLifecycle()
    val effect = rememberFlowWithLifecycle(viewModel.effect)

    val clipboardManager = LocalClipboardManager.current
    var showFollowerDialog by remember { mutableStateOf(false) }
    var showFollowingDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showLoginDialog by remember { mutableStateOf(false) }
    var loginDialogState by remember { mutableStateOf(LoginScreenState.INITIAL) }
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(effect) {
        effect.collect { action ->
            when (action) {
                is AccountEffect.ShowSnackbar -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = action.text,
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                is AccountEffect.CopyRoomNumber -> {
                    clipboardManager.setText(AnnotatedString(action.roomNumber))
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = getString(Res.string.copy_room_snackbar),
                            duration = SnackbarDuration.Short
                        )
                    }
                }
                
                is AccountEffect.NavigateToScreen -> {
                    navController.navigateTo(action.destination)
                }

                is AccountEffect.ControlLoginDialog -> {
                    showLoginDialog = action.visible
                    loginDialogState = LoginScreenState.INITIAL
                }

                is AccountEffect.ControlDrawer -> {
                    coroutineScope.launch {
                        if (action.visible) {
                            drawerState.open()
                        } else {
                            drawerState.close()
                        }
                    }
                }

                is AccountEffect.ControlLoginDialogScreen -> {
                    loginDialogState = action.destination
                }

                is AccountEffect.PopBackLoginDialog -> {
                    loginDialogState = when (loginDialogState) {
                        LoginScreenState.REGISTER, LoginScreenState.FORGOT_PASSWORD -> LoginScreenState.PASSWORD_LOGIN
                        LoginScreenState.VERIFY_EMAIL -> LoginScreenState.REGISTER
                        else -> LoginScreenState.INITIAL
                    }
                }

                is AccountEffect.ControlFollowerDialog -> {
                    // lazy loading
                    if (action.visible) {
                        viewModel.sendEvent(GetFollowers())
                    }
                    showFollowerDialog = action.visible
                }

                is AccountEffect.ControlFollowingDialog -> {
                    if (action.visible) {
                        viewModel.sendEvent(GetFollowings())
                    }
                    showFollowingDialog = action.visible
                }

                is AccountEffect.ControlEditProfileDialog -> {
                    if (action.visible) {
                        viewModel.sendEvent(GetEditProfileData())
                    }
                    showEditProfileDialog = action.visible
                }
            }
        }
    }

    LoginDialog(
        isVisible = showLoginDialog,
        currentScreen = loginDialogState,
        onPopBack = { viewModel.sendEffect(AccountEffect.PopBackLoginDialog()) },
        onDismissRequest = { viewModel.sendEffect(AccountEffect.ControlLoginDialog(false)) },
        onHelp = { viewModel.sendEffect(AccountEffect.ControlLoginDialogScreen(LoginScreenState.HELP)) },
        onEnterLogin = { viewModel.sendEffect(AccountEffect.ControlLoginDialogScreen(LoginScreenState.PASSWORD_LOGIN)) },
        onEnterToken = { viewModel.sendEffect(AccountEffect.ControlLoginDialogScreen(LoginScreenState.TOKEN_LOGIN)) },
        onEnterRegister = { viewModel.sendEffect(AccountEffect.ControlLoginDialogScreen(LoginScreenState.REGISTER)) },
        onEnterForgot = { viewModel.sendEffect(AccountEffect.ControlLoginDialogScreen(LoginScreenState.FORGOT_PASSWORD)) },
        onLoginWithToken = { token ->
            viewModel.sendEvent(GetUserInfo(token))
        },
        onLoginWithPassword = { username, password ->
            viewModel.sendEvent(Login(username, password))
        },
        onRegister = { username, password, email ->
            viewModel.sendEvent(Signup(username, password, email))
        },
        onSendVerificationCode = {
            viewModel.sendEvent(SendVerificationCode())
        },
        onVerifyCode = { code ->
            viewModel.sendEvent(VerifyEmail(code))
        },
        sendCountDown = state.countDown
    )

    EditProfileDialog(
        isVisible = showEditProfileDialog,
        onDismissRequest = { viewModel.sendEffect(AccountEffect.ControlEditProfileDialog(false)) },
        avatar = state.accountInfo.accountSummary.avatar,
        profile = state.editProfileData,
    )

    FollowListDialog(
        isVisible = showFollowingDialog,
        title = "关注列表",
        onDismissRequest = { viewModel.sendEffect(AccountEffect.ControlFollowingDialog(false)) },
        followList = state.followings,
        onFollow = { viewModel.sendEvent(FollowUser(it)) },
        placeholder = {
            Box(
                contentAlignment = Alignment.Center,
            ) {
                Text("暂无")
            }
        }
    )

    FollowListDialog(
        isVisible = showFollowerDialog,
        title = "粉丝列表",
        onDismissRequest = { viewModel.sendEffect(AccountEffect.ControlFollowerDialog(false)) },
        followList = state.followers,
        onFollow = { viewModel.sendEvent(FollowUser(it)) },
        placeholder = {
            Box(
                contentAlignment = Alignment.Center,
            ) {
                Text("暂无（若仅使用Token登录，请注意本客户端无法获取账号原有粉丝列表）")
            }
        }
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(12.dp))
                    NavigationDrawerItem(
                        icon = { Icon(Icons.AutoMirrored.Default.Logout, contentDescription = null) },
                        label = { Text("退出登录") },
                        onClick = { viewModel.sendEvent(Logout()) },
                        selected = false,
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Token, contentDescription = null) },
                        label = { Text("重置令牌") },
                        onClick = {
                            viewModel.sendEffect(AccountEffect.ControlLoginDialog(true))
                            viewModel.sendEffect(AccountEffect.ControlLoginDialogScreen(LoginScreenState.TOKEN_LOGIN))
                        },
                        selected = false,
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        content = {
            SuiteScaffold(
                isExpanded = isExpanded,
                screens = Screen.bottomScreenList,
                currentDestination = navBackStackEntry?.destination,
                onNavigateTo = navController::navigateTo,
                snackbarHost = {
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier
                            .zIndex(Float.MAX_VALUE)
                            .imePadding()
                    )
                }
            ) { paddingValues ->
                if (state.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                } else {
                    UserProfile(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        accountInfo = state.accountInfo,
                        sideButton = {
                            EditAccountButton(
                                isLoggedIn = state.isLoggedIn,
                                onLogIn = { viewModel.sendEffect(AccountEffect.ControlLoginDialog(true)) },
                                onEditAccount = {
                                    viewModel.sendEffect(AccountEffect.ControlEditProfileDialog(true))
                                },
                            )
                        },
                        onBrowseFollowings = { viewModel.sendEffect(AccountEffect.ControlFollowingDialog(true)) },
                        onBrowseFollowers = { viewModel.sendEffect(AccountEffect.ControlFollowerDialog(true)) },
                    )
                }
            }
        }
    )
}

@Preview
@Composable
private fun AccountScreenPreview() {
    val navController = rememberNavController()
    AccountScreen(navController)
}