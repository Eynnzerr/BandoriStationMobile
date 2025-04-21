package com.eynnzerr.bandoristation.feature.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.copy_room_snackbar
import com.eynnzerr.bandoristation.navigation.Screen
import com.eynnzerr.bandoristation.navigation.ext.navigateTo
import com.eynnzerr.bandoristation.ui.common.LocalAppProperty
import com.eynnzerr.bandoristation.ui.component.AppNavBar
import com.eynnzerr.bandoristation.ui.component.EditAccountButton
import com.eynnzerr.bandoristation.ui.dialog.LoginDialog
import com.eynnzerr.bandoristation.ui.component.SimpleRoomCard
import com.eynnzerr.bandoristation.ui.component.SuiteScaffold
import com.eynnzerr.bandoristation.ui.component.UserAvatar
import com.eynnzerr.bandoristation.ui.component.UserBannerImage
import com.eynnzerr.bandoristation.ui.dialog.LoginScreenState
import com.eynnzerr.bandoristation.ui.ext.appBarScroll
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
    val isExpanded = LocalAppProperty.current.screenInfo.isExpanded()

    val state by viewModel.state.collectAsStateWithLifecycle()
    val effect = rememberFlowWithLifecycle(viewModel.effect)

    val tabs = listOf("发布历史", "玩家信息")
    val clipboardManager = LocalClipboardManager.current
    var selectedTabIndex by remember { mutableStateOf(0) }
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
            viewModel.sendEvent(AccountIntent.GetUserInfo(token))
        },
        onLoginWithPassword = { username, password ->
            viewModel.sendEvent(AccountIntent.Login(username, password))
        },
        onRegister = { username, password, email ->
            viewModel.sendEvent(AccountIntent.Signup(username, password, email))
        },
        onSendVerificationCode = {
            viewModel.sendEvent(AccountIntent.SendVerificationCode())
        },
        onVerifyCode = { code ->
            viewModel.sendEvent(AccountIntent.VerifyEmail(code))
        },
        sendCountDown = state.countDown
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
                        onClick = { viewModel.sendEvent(AccountIntent.Logout()) },
                        selected = false,
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Token, contentDescription = null) },
                        label = { Text("重置令牌") },
                        onClick = {  },
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        UserBannerImage(
                            bannerName = state.accountInfo.summary.banner,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                        )

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
                                    avatarName = state.accountInfo.summary.avatar,
                                    size = 64.dp
                                )

                                // 用户信息
                                Column(
                                    modifier = Modifier
                                        .padding(start = 12.dp)
                                        .padding(top = 4.dp)
                                ) {
                                    Text(
                                        text = state.accountInfo.summary.username,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                    Text(
                                        text = "UID: ${state.accountInfo.summary.userId}",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(top = 4.dp),
                                    )
                                    Row(
                                        modifier = Modifier.padding(top = 0.dp),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        // TODO 点击打开关注列表
                                        Text(
                                            text = "${state.accountInfo.summary.following} 关注",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "${state.accountInfo.summary.follower} 关注者",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }

                            EditAccountButton(
                                isLoggedIn = state.isLoggedIn,
                                onLogIn = { viewModel.sendEffect(AccountEffect.ControlLoginDialog(true)) },
                                onEditAccount = {
                                    // TODO 打开资料编辑对话框 或者 模态框
                                },
                            )
                        }

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
                                // 发布历史
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(8.dp)
                                ) {
                                    itemsIndexed(state.accountInfo.roomNumberHistory) { index, roomInfo ->
                                        SimpleRoomCard(
                                            number = roomInfo.number,
                                            rawMessage = roomInfo.rawMessage,
                                            timestamp = roomInfo.time,
                                            sourceName = roomInfo.sourceName,
                                            avatarName = state.accountInfo.summary.avatar,
                                            userName = state.accountInfo.summary.username,
                                            onCopy = { roomNumber ->
                                                viewModel.sendEffect(AccountEffect.CopyRoomNumber(roomNumber))
                                            },
                                        )
                                    }
                                }
                            }
                            1 -> {
                                // 玩家信息
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "即将推出",
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }

//            Scaffold(
//                bottomBar = {
//                    AppNavBar(
//                        screens = Screen.bottomScreenList,
//                        currentDestination = navBackStackEntry?.destination,
//                        onNavigateTo = navController::navigateTo,
//                    )
//                },
//                snackbarHost = {
//                    SnackbarHost(
//                        hostState = snackbarHostState,
//                        modifier = Modifier
//                            .zIndex(Float.MAX_VALUE)
//                            .imePadding()
//                    )
//                }
//            ) { paddingValues ->
//                if (state.isLoading) {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(paddingValues)
//                    ) {
//                        CircularProgressIndicator(
//                            modifier = Modifier.align(Alignment.Center)
//                        )
//                    }
//                } else {
//                    Column(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(paddingValues)
//                    ) {
//                        UserBannerImage(
//                            bannerName = state.accountInfo.summary.banner,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(150.dp)
//                        )
//
//                        // 用户信息区域
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(horizontal = 16.dp)
//                                .padding(top = 16.dp),
//                            horizontalArrangement = Arrangement.SpaceBetween,
//                            verticalAlignment = Alignment.Top
//                        ) {
//                            Row(verticalAlignment = Alignment.CenterVertically) {
//                                // 用户头像
//                                UserAvatar(
//                                    avatarName = state.accountInfo.summary.avatar,
//                                    size = 64.dp
//                                )
//
//                                // 用户信息
//                                Column(
//                                    modifier = Modifier
//                                        .padding(start = 12.dp)
//                                        .padding(top = 4.dp)
//                                ) {
//                                    Text(
//                                        text = state.accountInfo.summary.username,
//                                        fontWeight = FontWeight.Bold,
//                                        fontSize = 20.sp
//                                    )
//                                    Text(
//                                        text = "UID: ${state.accountInfo.summary.userId}",
//                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
//                                        fontSize = 14.sp,
//                                        modifier = Modifier.padding(top = 4.dp),
//                                    )
//                                    Row(
//                                        modifier = Modifier.padding(top = 0.dp),
//                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
//                                    ) {
//                                        // TODO 点击打开关注列表
//                                        Text(
//                                            text = "${state.accountInfo.summary.following} 关注",
//                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
//                                            fontSize = 14.sp
//                                        )
//                                        Text(
//                                            text = "${state.accountInfo.summary.follower} 关注者",
//                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
//                                            fontSize = 14.sp
//                                        )
//                                    }
//                                }
//                            }
//
//                            EditAccountButton(
//                                isLoggedIn = state.isLoggedIn,
//                                onLogIn = { viewModel.sendEffect(AccountEffect.ControlLoginDialog(true)) },
//                                onEditAccount = {
//                                    // TODO 打开资料编辑对话框 或者 模态框
//                                },
//                            )
//                        }
//
//                        Spacer(modifier = Modifier.height(16.dp))
//
//                        // 标签页
//                        TabRow(
//                            selectedTabIndex = selectedTabIndex,
//                            containerColor = MaterialTheme.colorScheme.surface,
//                            contentColor = MaterialTheme.colorScheme.primary
//                        ) {
//                            tabs.forEachIndexed { index, title ->
//                                Tab(
//                                    selected = selectedTabIndex == index,
//                                    onClick = { selectedTabIndex = index },
//                                    text = { Text(title) }
//                                )
//                            }
//                        }
//
//                        // 标签页内容
//                        when (selectedTabIndex) {
//                            0 -> {
//                                // 发布历史
//                                LazyColumn(
//                                    modifier = Modifier.fillMaxSize(),
//                                    contentPadding = PaddingValues(8.dp)
//                                ) {
//                                    itemsIndexed(state.accountInfo.roomNumberHistory) { index, roomInfo ->
//                                        SimpleRoomCard(
//                                            number = roomInfo.number,
//                                            rawMessage = roomInfo.rawMessage,
//                                            timestamp = roomInfo.time,
//                                            sourceName = roomInfo.sourceName,
//                                            avatarName = state.accountInfo.summary.avatar,
//                                            userName = state.accountInfo.summary.username,
//                                            onCopy = { roomNumber ->
//                                                viewModel.sendEffect(AccountEffect.CopyRoomNumber(roomNumber))
//                                            },
//                                        )
//                                    }
//                                }
//                            }
//                            1 -> {
//                                // 玩家信息
//                                Box(
//                                    modifier = Modifier
//                                        .fillMaxSize()
//                                        .padding(16.dp),
//                                    contentAlignment = Alignment.Center
//                                ) {
//                                    Text(
//                                        text = "即将推出",
//                                        color = Color.Gray
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
//            }
        }
    )
}

@Preview
@Composable
private fun AccountScreenPreview() {
    val navController = rememberNavController()
    AccountScreen(navController)
}