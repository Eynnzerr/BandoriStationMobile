package com.eynnzerr.bandoristation.feature.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.copy_room_snackbar
import bandoristationm.composeapp.generated.resources.home_screen_title
import bandoristationm.composeapp.generated.resources.placeholder
import com.eynnzerr.bandoristation.feature.home.HomeIntent.*
import com.eynnzerr.bandoristation.navigation.Screen
import com.eynnzerr.bandoristation.navigation.ext.navigateTo
import com.eynnzerr.bandoristation.ui.common.LocalAppProperty
import com.eynnzerr.bandoristation.ui.component.AppNavBar
import com.eynnzerr.bandoristation.ui.component.AppTopBar
import com.eynnzerr.bandoristation.ui.component.CurrentRoomHeader
import com.eynnzerr.bandoristation.ui.component.RoomCard
import com.eynnzerr.bandoristation.ui.component.SuiteScaffold
import com.eynnzerr.bandoristation.ui.dialog.SendRoomDialog
import com.eynnzerr.bandoristation.ui.ext.appBarScroll
import com.eynnzerr.bandoristation.utils.rememberFlowWithLifecycle
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
    val isExpanded = LocalAppProperty.current.screenInfo.isExpanded()
    val lazyListState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()
    var wasAtTop by remember { mutableStateOf(true) }
    var showSendRoomDialog by remember { mutableStateOf(false) }

    var prefillRoomNumber by remember { mutableStateOf("") }
    var prefillDescription by remember { mutableStateOf("") }

    // Determine if the first item is visible
    val isFirstItemVisible by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0
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

                is HomeEffect.ShowSnackbar -> {
                    coroutineScope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = getString(action.textRes),
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                is HomeEffect.OpenSendRoomDialog -> {
                    prefillRoomNumber = action.prefillRoomNumber
                    prefillDescription = action.prefillDescription
                    showSendRoomDialog = true
                }
                is HomeEffect.CloseSendRoomDialog -> {
                    showSendRoomDialog = false
                    prefillRoomNumber = ""
                    prefillDescription = ""
                }
            }
        }
    }

    SendRoomDialog(
        isVisible = showSendRoomDialog,
        onDismissRequest = { viewModel.sendEffect(HomeEffect.CloseSendRoomDialog()) },
        onSendClick = { roomInfo -> viewModel.sendEvent(UploadRoom(roomInfo)) },
        onAddPresetWord = { viewModel.sendEvent(AddPresetWord(it)) },
        onDeletePresetWord = { viewModel.sendEvent(RemovePresetWord(it)) },
        presetWords = state.presetWords,
        prefillRoomNumber = prefillRoomNumber,
        prefillDescription = prefillDescription,
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
                title = stringResource(Res.string.home_screen_title),
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
                            // TODO 预测线
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.TrendingUp,
                            contentDescription = ""
                        )
                    }

                    IconButton(
                        onClick = {
                            // TODO 排序
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Sort,
                            contentDescription = ""
                        )
                    }

                    IconButton(
                        onClick = {
                            // TODO 过滤
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
            Column {
                AnimatedVisibility(
                    visible = !isFirstItemVisible && state.rooms.isNotEmpty(),
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut() + slideOutVertically { it },
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    FloatingActionButton(
                        onClick = {
                            viewModel.sendEffect(HomeEffect.ScrollToFirst())
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Return to top"
                        )
                    }
                }

                FloatingActionButton(
                    onClick = {
                        viewModel.sendEffect(HomeEffect.OpenSendRoomDialog())
                    }
                ) {
                    Icon(Icons.Filled.Add, "add student menu")
                }
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
            state.selectedRoom?.let {
                stickyHeader(key = -1) {
                    CurrentRoomHeader(
                        roomInfo = state.selectedRoom!!,
                        currentTimeMillis = state.localTimestampMillis,
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
                    isJoined = roomInfo == state.selectedRoom,
                    currentTimeMillis = state.localTimestampMillis,
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

private const val TAG = "HomeScreen"