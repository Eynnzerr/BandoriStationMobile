package com.eynnzerr.bandoristation.feature.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.chat_message_input_placeholder
import com.eynnzerr.bandoristation.navigation.Screen
import com.eynnzerr.bandoristation.navigation.ext.navigateTo
import com.eynnzerr.bandoristation.ui.common.LocalAppProperty
import com.eynnzerr.bandoristation.ui.component.app.AppTopBar
import com.eynnzerr.bandoristation.ui.component.chat.ArrowHorizontalPosition
import com.eynnzerr.bandoristation.ui.component.chat.ChatPiece
import com.eynnzerr.bandoristation.ui.component.app.SuiteScaffold
import com.eynnzerr.bandoristation.ui.component.chat.TimePiece
import com.eynnzerr.bandoristation.ui.component.chat.UnreadBubble
import com.eynnzerr.bandoristation.ui.dialog.UserProfileDialog
import com.eynnzerr.bandoristation.ui.ext.appBarScroll
import com.eynnzerr.bandoristation.utils.rememberFlowWithLifecycle
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.eynnzerr.bandoristation.ui.component.chat.BottomTextBar
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavHostController,
    viewModel: ChatViewModel = koinViewModel<ChatViewModel>()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val effect = rememberFlowWithLifecycle(viewModel.effect)

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var showProfileDialog by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val pullToRefreshState = rememberPullToRefreshState()
    val scaleFraction = {
        if (state.isLoading) 1f
        else LinearOutSlowInEasing.transform(pullToRefreshState.distanceFraction).coerceIn(0f, 1f)
    }

    LaunchedEffect(effect) {
        effect.collect { action ->
            when (action) {
                is ChatEffect.ScrollToLatest -> {
                    if (state.chats.isNotEmpty())
                        lazyListState.animateScrollToItem(state.chats.size - 1)
                }

                is ChatEffect.ShowResSnackbar -> {
                    coroutineScope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = getString(action.textRes),
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                is ChatEffect.NavigateToScreen -> {
                    navController.navigateTo(action.destination)
                }

                is ChatEffect.ShowSnackbar -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = action.text,
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                is ChatEffect.ControlProfileDialog -> {
                    showProfileDialog = action.visible
                }
            }
        }
    }

    // automatically scroll to bottom after chat connection initialization.
    LaunchedEffect(state.initialized) {
        if (state.initialized) {
            viewModel.sendEffect(ChatEffect.ScrollToLatest())
        }
    }

    // automatically scroll to bottom if last chat is self sent.
    LaunchedEffect(state.chats) {
        val lastChat = state.chats.lastOrNull()
        lastChat?.let {
            if (it.userInfo.userId == state.selfId) {
                viewModel.sendEffect(ChatEffect.ScrollToLatest())
            }
        }
    }

    // Check if the list is at the bottom
    val isAtBottom by remember {
        derivedStateOf {
            val lastVisibleItem = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()
            val lastItem = lazyListState.layoutInfo.totalItemsCount - 1

            if (lastVisibleItem == null || lastItem < 0) {
                true // Empty list or no items, consider it at bottom
            } else {
                lastVisibleItem.index >= lastItem
            }
        }
    }

    // Remove unread bubble if scrolled to bottom
    LaunchedEffect(isAtBottom) {
        if (state.initialized && isAtBottom) {
            viewModel.sendEvent(ChatIntent.ClearUnreadCount())
        }
    }

    UserProfileDialog(
        isVisible = showProfileDialog,
        accountInfo = state.selectedUser,
        onDismissRequest = { viewModel.sendEffect(ChatEffect.ControlProfileDialog(false)) },
        onFollow = { viewModel.sendEvent(ChatIntent.FollowUser(it)) },
        hasFollowed = state.selectedUser.accountSummary.userId in state.followingUsers,
    )

    Scaffold(
        modifier = Modifier
            .appBarScroll(true, scrollBehavior)
            .pullToRefresh(
                state = pullToRefreshState,
                isRefreshing = state.isLoading,
                onRefresh = { viewModel.sendEvent(ChatIntent.LoadMore()) }
            ),
        topBar = {
            AppTopBar(
                title = stringResource(state.title),
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.sendEffect(ChatEffect.NavigateToScreen(Screen.Settings)) }
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
                            viewModel.sendEvent(ChatIntent.Reset())
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = ""
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomTextBar(
                onSend = {
                    viewModel.sendEvent(ChatIntent.SendChat(it))
                }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.sendEffect(ChatEffect.ScrollToLatest())
                },
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .zIndex(1f)
                    .animateFloatingActionButton(
                        visible = !isAtBottom,
                        alignment = Alignment.BottomCenter,
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = ""
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .padding(16.dp),
            ) {
                itemsIndexed(
                    items = state.chats,
                    key = { _, item -> item.localId }
                ) { index, chatMessage ->
                    Row(
                        modifier = Modifier.animateItem()
                    ) {
                        if (chatMessage.userInfo.userId == null) {
                            // System Message like date separator
                            TimePiece(
                                chatMessage = chatMessage,
                            )
                        } else {
                            // User Message
                            ChatPiece(
                                chatMessage = chatMessage,
                                isMyMessage = chatMessage.userInfo.userId == state.selfId,
                                onClickAvatar = {
                                    viewModel.sendEvent(ChatIntent.BrowseUser(chatMessage.userInfo.userId))
                                }
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = state.unreadCount > 0,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = 8.dp
                    )
                    .zIndex(1f)
            ) {
                UnreadBubble(
                    text = state.unreadCount.toString(),
                    isArrowOnTop = false,
                    arrowPosition = ArrowHorizontalPosition.END,
                )
            }

            Box(
                Modifier.align(Alignment.TopCenter).graphicsLayer {
                    scaleX = scaleFraction()
                    scaleY = scaleFraction()
                }
            ) {
                PullToRefreshDefaults.LoadingIndicator(state = pullToRefreshState, isRefreshing = state.isLoading)
            }
        }
    }
}

@Preview
@Composable
fun ChatScreenPreview() {
    val navController = rememberNavController()
    ChatScreen(navController)
}
