package com.eynnzerr.bandoristation.feature.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.chat_screen_title
import com.eynnzerr.bandoristation.navigation.Screen
import com.eynnzerr.bandoristation.navigation.ext.navigateTo
import com.eynnzerr.bandoristation.ui.component.AppNavBar
import com.eynnzerr.bandoristation.ui.component.AppTopBar
import com.eynnzerr.bandoristation.ui.component.ArrowHorizontalPosition
import com.eynnzerr.bandoristation.ui.component.ChatPiece
import com.eynnzerr.bandoristation.ui.component.TimePiece
import com.eynnzerr.bandoristation.ui.component.UnreadBubble
import com.eynnzerr.bandoristation.ui.ext.appBarScroll
import com.eynnzerr.bandoristation.utils.rememberFlowWithLifecycle
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavHostController,
    viewModel: ChatViewModel = koinViewModel<ChatViewModel>()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val effect = rememberFlowWithLifecycle(viewModel.effect)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var messageText by remember { mutableStateOf("") }
    val lazyListState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(effect) {
        effect.collect { action ->
            when (action) {
                is ChatEffect.ScrollToLatest -> {
                    if (state.chats.isNotEmpty())
                        lazyListState.animateScrollToItem(state.chats.size - 1)
                }

                is ChatEffect.ShowSnackbar -> {
                    coroutineScope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = getString(action.textRes),
                            duration = SnackbarDuration.Short
                        )
                    }
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
        if (state.initialized) {
            viewModel.sendEvent(ChatIntent.ClearUnreadCount())
        }
    }

    Scaffold(
        modifier = Modifier.appBarScroll(true, scrollBehavior),
        topBar = {
            AppTopBar(
                title = stringResource(Res.string.chat_screen_title),
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(
                        onClick = {
                            // TODO Navigate to settings
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
                            viewModel.sendEvent(ChatIntent.ClearAll())
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "clear messages"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("输入消息...") },
                        maxLines = 3
                    )
                    IconButton(
                        onClick = {
                            // Handle send message
                            if (messageText.isNotBlank()) {
                                viewModel.sendEvent(ChatIntent.SendChat(messageText))
                                messageText = ""
                            }
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "发送"
                        )
                    }
                }

                AppNavBar(
                    screens = Screen.bottomScreenList,
                    currentDestination = navBackStackEntry?.destination,
                    onNavigateTo = navController::navigateTo,
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            PullToRefreshBox(
                isRefreshing = state.isLoading,
                onRefresh = { viewModel.sendEvent(ChatIntent.LoadMore()) },
            ) {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .padding(16.dp),
                ) {
                    itemsIndexed(
                        items = state.chats,
                        key = { index, item ->
                            val timestampPart = item.timestamp
                            val contentPart = item.content.hashCode()
                            val userPart = item.userInfo.hashCode()

                            "$timestampPart-$contentPart-$userPart"
                        }
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
                                    isMyMessage = chatMessage.userInfo.userId == state.selfId
                                )
                            }
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
                        start = 24.dp,
                        bottom = innerPadding.calculateBottomPadding() + 32.dp
                    )
                    .zIndex(1f)
            ) {
                UnreadBubble(
                    text = state.unreadCount.toString(),
                    isArrowOnTop = false,
                    arrowPosition = ArrowHorizontalPosition.END,
                )
            }

            AnimatedVisibility(
                visible = !isAtBottom,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
                    .zIndex(1f)
            ) {
                FloatingActionButton(
                    onClick = {
                        viewModel.sendEffect(ChatEffect.ScrollToLatest())
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "滚动到底部"
                    )
                }
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