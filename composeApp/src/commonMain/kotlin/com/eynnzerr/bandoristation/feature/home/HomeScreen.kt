package com.eynnzerr.bandoristation.feature.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.copy_room_snackbar
import bandoristationm.composeapp.generated.resources.request_room_dialog_copy_button
import com.eynnzerr.bandoristation.feature.home.HomeIntent.*
import com.eynnzerr.bandoristation.model.room.RoomInfo
import com.eynnzerr.bandoristation.model.UserInfo
import com.eynnzerr.bandoristation.navigation.Screen
import com.eynnzerr.bandoristation.navigation.ext.navigateTo
import com.eynnzerr.bandoristation.ui.common.LocalAppProperty
import com.eynnzerr.bandoristation.ui.component.app.AppTopBar
import com.eynnzerr.bandoristation.ui.component.room.CurrentRoomHeader
import com.eynnzerr.bandoristation.ui.component.room.RoomCard
import com.eynnzerr.bandoristation.ui.component.app.SuiteScaffold
import com.eynnzerr.bandoristation.ui.dialog.BlockUserDialog
import com.eynnzerr.bandoristation.ui.dialog.HelpDialog
import com.eynnzerr.bandoristation.ui.dialog.InformDialog
import com.eynnzerr.bandoristation.ui.dialog.RequestRoomDialog
import com.eynnzerr.bandoristation.ui.dialog.RoomFilterDialog
import com.eynnzerr.bandoristation.ui.dialog.UpdateDialog
import com.eynnzerr.bandoristation.ui.dialog.SendRoomDialog
import com.eynnzerr.bandoristation.ui.ext.appBarScroll
import com.eynnzerr.bandoristation.utils.rememberFlowWithLifecycle
import com.eynnzerr.bandoristation.model.GithubRelease
import com.eynnzerr.bandoristation.ui.animation.ExpressiveVisibility
import com.eynnzerr.bandoristation.ui.dialog.ApproveRequestDialog
import com.eynnzerr.bandoristation.ui.dialog.UserProfileDialog
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = koinViewModel<HomeViewModel>()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val effect = rememberFlowWithLifecycle(viewModel.effect)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val uriHandler = LocalUriHandler.current
    val isExpanded = LocalAppProperty.current.screenInfo.isLandscape()
    val lazyListState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()
    var wasAtTop by remember { mutableStateOf(true) }
    var showInformUserDialog by remember { mutableStateOf(false) }
    var showSendRoomDialog by remember { mutableStateOf(false) }
    var showFilterRoomDialog by remember { mutableStateOf(false) }
    var showBlockUserDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var latestRelease by remember { mutableStateOf<GithubRelease?>(null) }
    var roomToInform : RoomInfo? by remember { mutableStateOf(null) }
    var userToBlock: UserInfo? by remember { mutableStateOf(null) }
    var prefillRoomNumber by remember { mutableStateOf("") }
    var prefillDescription by remember { mutableStateOf("") }
    var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }

    val isFirstItemVisible by remember {
        derivedStateOf {
            !lazyListState.canScrollBackward
        }
    }

    // record previous list location, before reacting to list change.
    LaunchedEffect(isFirstItemVisible) {
        wasAtTop = isFirstItemVisible
    }

    // Automatically keeping at top when new rooms arrive, if was already at top.
    LaunchedEffect(state.rooms) {
        if (wasAtTop) {
            viewModel.sendEffect(HomeEffect.ScrollToFirst())
        }
    }

    LaunchedEffect(effect) {
        effect.collect { action ->
            when (action) {
                is HomeEffect.CopyRoomNumber -> {
                    clipboardManager.setText(AnnotatedString(action.roomNumber))
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = getString(Res.string.copy_room_snackbar),
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                is HomeEffect.NavigateToScreen -> {
                    if (action.destination is Screen.Chat) {
                        viewModel.sendEvent(UpdateMessageBadge(false))
                    }
                    navController.navigateTo(action.destination)
                }

                is HomeEffect.ScrollToFirst -> {
                    coroutineScope.launch {
                        lazyListState.animateScrollToItem(0)
                    }
                }

                is HomeEffect.ShowResourceSnackbar -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = getString(action.textRes),
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                is HomeEffect.ShowRequestResultBySnackbar -> {
                    coroutineScope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = action.text,
                            duration = SnackbarDuration.Short,
                            actionLabel = getString(Res.string.request_room_dialog_copy_button),
                            withDismissAction = true
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            clipboardManager.setText(AnnotatedString(action.number))
                        }
                    }
                }

                is HomeEffect.OpenSendRoomDialog -> {
                    prefillRoomNumber = action.prefillRoomNumber
                    prefillDescription = action.prefillDescription.replaceFirst("^\\d{6}\\s*".toRegex(), "")
                    showSendRoomDialog = true
                }

                is HomeEffect.CloseSendRoomDialog -> {
                    showSendRoomDialog = false
                    prefillRoomNumber = ""
                    prefillDescription = ""
                }

                is HomeEffect.OpenInformUserDialog -> {
                    roomToInform = action.roomToInform
                    showInformUserDialog = true
                }

                is HomeEffect.CloseInformUserDialog -> {
                    roomToInform = null
                    showInformUserDialog = false
                }

                is HomeEffect.CloseFilterDialog -> {
                    showFilterRoomDialog = false
                }

                is HomeEffect.OpenFilterDialog -> {
                    showFilterRoomDialog = true
                }

                is HomeEffect.CloseBlockUserDialog -> {
                    userToBlock = null
                    showBlockUserDialog = false
                }

                is HomeEffect.OpenBlockUserDialog -> {
                    userToBlock = action.userToBlock
                    showBlockUserDialog = true
                }

                is HomeEffect.CloseHelpDialog -> {
                    showHelpDialog = false
                }

                is HomeEffect.OpenHelpDialog -> {
                    showHelpDialog = true
                }

                is HomeEffect.OpenUpdateDialog -> {
                    latestRelease = action.release
                    showUpdateDialog = true
                }

                is HomeEffect.CloseUpdateDialog -> {
                    showUpdateDialog = false
                    latestRelease = null
                }

                is HomeEffect.ShowSnackbar -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = action.text,
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                is HomeEffect.ControlProfileDialog -> {
                    showProfileDialog = action.visible
                }
            }
        }
    }

    val currentRequest = state.accessRequestQueue.firstOrNull()
    ApproveRequestDialog(
        isVisible = currentRequest != null,
        request = currentRequest,
        onDismissRequest = { addToWhitelist, addToBlacklist ->
            viewModel.sendEvent(
                RespondToAccessRequest(
                    isApproved = false,
                    addToBlacklist = addToBlacklist,
                    addToWhiteList = addToWhitelist
                )
            )
        },
        onConfirm = { addToWhitelist, addToBlacklist ->
            viewModel.sendEvent(
                RespondToAccessRequest(
                    isApproved = true,
                    addToBlacklist = addToBlacklist,
                    addToWhiteList = addToWhitelist
                )
            )
        },
        onViewUser = {
            viewModel.sendEvent(BrowseUser(currentRequest?.requesterId?.toLongOrNull() ?: -1))
        }
    )

    RequestRoomDialog(
        isVisible = state.showRequestRoomDialog,
        state = state.requestRoomState,
        roomNumber = state.decryptedRoomNumber,
        errorMessage = state.requestRoomError,
        onDismissRequest = { viewModel.sendEvent(OnDismissRequestRoomDialog()) },
        onSubmitInviteCode = { code ->
            viewModel.sendEvent(OnSubmitInviteCode(
                targetUser = state.requestingRoomInfo?.userInfo?.userId?.toString() ?: "",
                code = code)
            )
        },
        onApplyOnline = { viewModel.sendEvent(OnApplyOnline(state.requestingRoomInfo)) },
        onCopy = { viewModel.sendEffect(HomeEffect.CopyRoomNumber(it)) }
    )

    SendRoomDialog(
        isVisible = showSendRoomDialog,
        onDismissRequest = { viewModel.sendEffect(HomeEffect.CloseSendRoomDialog()) },
        onSendClick = { roomNumber, description, continuous, encrypted ->
            viewModel.sendEvent(UploadRoom(roomNumber, description, continuous, encrypted))
        },
        onAddPresetWord = { viewModel.sendEvent(AddPresetWord(it)) },
        onDeletePresetWord = { viewModel.sendEvent(RemovePresetWord(it)) },
        presetWords = state.presetWords,
        prefillRoomNumber = prefillRoomNumber,
        prefillDescription = prefillDescription,
    )

    InformDialog(
        isVisible = showInformUserDialog,
        selectedRoom = roomToInform,
        onDismissRequest = { viewModel.sendEffect(HomeEffect.CloseInformUserDialog()) },
        onConfirm = { viewModel.sendEvent(InformUser(it)) }
    )

    RoomFilterDialog(
        isVisible = showFilterRoomDialog,
        presetWords = state.roomFilter.keyword,
        presetUsers = state.roomFilter.user,
        onDismissRequest = { viewModel.sendEffect(HomeEffect.CloseFilterDialog()) },
        onConfirm = { viewModel.sendEvent(UpdateRoomFilter(it)) }
    )

    BlockUserDialog(
        isVisible = showBlockUserDialog,
        selectedUser = userToBlock,
        onDismissRequest = { viewModel.sendEffect(HomeEffect.CloseBlockUserDialog()) },
        onConfirm = {
            userToBlock?.let {
                val filter = state.roomFilter.copy(user = state.roomFilter.user + it)
                viewModel.sendEvent(UpdateRoomFilter(filter))
            }
        }
    )

    HelpDialog(
        isVisible = showHelpDialog,
        markdownPath = "files/help.md",
        onDismissRequest = { viewModel.sendEffect(HomeEffect.CloseHelpDialog()) },
    )

    UpdateDialog(
        isVisible = showUpdateDialog,
        release = latestRelease,
        onDismissRequest = { viewModel.sendEffect(HomeEffect.CloseUpdateDialog()) },
        onConfirm = { url ->
            uriHandler.openUri(url)
            viewModel.sendEffect(HomeEffect.CloseUpdateDialog())
        }
    )

    UserProfileDialog(
        isVisible = showProfileDialog,
        accountInfo = state.selectedUser,
        onDismissRequest = { viewModel.sendEffect(HomeEffect.ControlProfileDialog(false)) },
        onFollow = { viewModel.sendEvent(FollowUser(it)) },
        hasFollowed = state.selectedUser.accountSummary.userId in state.followingUsers,
    )

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    SuiteScaffold(
        scaffoldModifier = Modifier.appBarScroll(true, scrollBehavior),
        isExpanded = isExpanded,
        screens = Screen.bottomScreenList,
        showBadges = listOf(false, state.hasUnReadMessages, false),
        currentDestination = navBackStackEntry?.destination,
        onNavigateTo = { viewModel.sendEffect(HomeEffect.NavigateToScreen(it)) },
        topBar = {
            AppTopBar(
                title = stringResource(state.title),
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.sendEffect(HomeEffect.NavigateToScreen(Screen.Settings))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = ""
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.sendEffect(HomeEffect.OpenHelpDialog())
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                            contentDescription = "",
                        )
                    }

                    IconButton(
                        onClick = {
                            viewModel.sendEvent(RefreshRooms())
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = ""
                        )
                    }

                    IconButton(
                        onClick = {
                            viewModel.sendEffect(HomeEffect.OpenFilterDialog())
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FilterAlt,
                            contentDescription = ""
                        )
                    }
                }
            )
        },
        bottomBar = {},
        floatingActionButton = {
            FloatingActionButtonMenu(
                expanded = fabMenuExpanded,
                button = {
                    ToggleFloatingActionButton(
                        checked = fabMenuExpanded,
                        onCheckedChange = { fabMenuExpanded = !fabMenuExpanded }
                    ) {
                        val imageVector by remember {
                            derivedStateOf {
                                if (checkedProgress > 0.5f) Icons.Filled.Close else Icons.Filled.Add
                            }
                        }
                        Icon(
                            painter = rememberVectorPainter(imageVector),
                            contentDescription = null,
                            modifier = Modifier.animateIcon({ checkedProgress }),
                        )
                    }
                },
                horizontalAlignment = Alignment.End,
            ) {
                FloatingActionButtonMenuItem(
                    onClick = {  },
                    icon = { Icon(Icons.AutoMirrored.Filled.Chat, null) },
                    text = { Text("创建群聊") }
                )

                FloatingActionButtonMenuItem(
                    onClick = { viewModel.sendEffect(HomeEffect.OpenSendRoomDialog()) },
                    icon = { Icon(Icons.Filled.Upload, null) },
                    text = { Text("上传房间") }
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            state = lazyListState,
        ) {
            stickyHeader(key = -1) {
                Box(modifier = Modifier.size(1.dp)) // 防止无RoomHeader时错误的滑动逻辑
                ExpressiveVisibility(visible = state.selectedRoom != null) {
                    CurrentRoomHeader(
                        roomInfo = state.selectedRoom ?: RoomInfo(),
                        currentTimeMillis = state.serverTimestampMillis,
                        startTimeMillis = state.joinedTimestampMillis,
                        onCopy = { roomNumber ->
                            viewModel.sendEffect(HomeEffect.CopyRoomNumber(roomNumber))
                        },
                        onPublish = {
                            state.selectedRoom?.let { selectedRoom ->
                                viewModel.sendEffect(
                                    HomeEffect.OpenSendRoomDialog(
                                        prefillRoomNumber = selectedRoom.number ?: "",
                                        prefillDescription = selectedRoom.rawMessage ?: ""
                                    )
                                )
                            }
                        },
                        onLeave = {
                            viewModel.sendEvent(JoinRoom(null))
                        },
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ExtendedFloatingActionButton(
                        text = { Text("回到顶部", style = MaterialTheme.typography.labelLarge) },
                        icon = { Icon(Icons.Rounded.ArrowUpward, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        onClick = { viewModel.sendEffect(HomeEffect.ScrollToFirst()) },
                        shape = MaterialTheme.shapes.extraLarge,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                        modifier = Modifier
                            .heightIn(max = 40.dp)
                            .animateFloatingActionButton(
                                visible = !isFirstItemVisible && state.rooms.isNotEmpty(),
                                alignment = Alignment.BottomEnd,
                            ),
                    )
                }
            }

            itemsIndexed(
                items = state.rooms.asReversed(),
                key = { index, item ->
                    state.rooms.size - 1 - index // reverse index
                }
            ) { index, roomInfo ->
                RoomCard(
                    roomInfo = roomInfo,
                    onCopy = { roomNumber ->
                        viewModel.sendEffect(HomeEffect.CopyRoomNumber(roomNumber))
                    },
                    onJoin = { joined ->
                        viewModel.sendEvent(JoinRoom(if (joined) roomInfo else null))
                    },
                    onBlockUser = {
                        roomInfo.userInfo?.let { viewModel.sendEffect(HomeEffect.OpenBlockUserDialog(it)) }
                    },
                    onReportUser = {
                        viewModel.sendEffect(HomeEffect.OpenInformUserDialog(roomInfo))
                    },
                    onClickUserAvatar = {
                        viewModel.sendEvent(BrowseUser(roomInfo.userInfo?.userId ?: -1))
                    },
                    isJoined = roomInfo == state.selectedRoom,
                    isEncrypted = roomInfo.number == "999999" && roomInfo.userInfo?.userId != state.userId,
                    onRequest = {
                        viewModel.sendEvent(OnRequestRoom(roomInfo))
                    },
                    currentTimeMillis = state.serverTimestampMillis,
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

private const val TAG = "HomeScreen"