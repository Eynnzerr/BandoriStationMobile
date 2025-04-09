package com.eynnzerr.bandoristation.feature.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.copy_room_snackbar
import bandoristationm.composeapp.generated.resources.home_screen_title
import com.eynnzerr.bandoristation.navigation.Screen
import com.eynnzerr.bandoristation.navigation.ext.navigateTo
import com.eynnzerr.bandoristation.ui.component.AppNavBar
import com.eynnzerr.bandoristation.ui.component.AppTopBar
import com.eynnzerr.bandoristation.ui.component.RoomCard
import com.eynnzerr.bandoristation.ui.ext.appBarScroll
import com.eynnzerr.bandoristation.utils.rememberFlowWithLifecycle
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = koinViewModel<HomeViewModel>()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val effect = rememberFlowWithLifecycle(viewModel.effect)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

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
                        viewModel.sendEvent(HomeIntent.UpdateMessageBadge(false))
                    }
                    navController.navigateTo(action.destination)
                }
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier.appBarScroll(true, scrollBehavior),
        topBar = {
            AppTopBar(
                title = stringResource(Res.string.home_screen_title),
                scrollBehavior = scrollBehavior,
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
        bottomBar = {
            AppNavBar(
                screens = Screen.bottomScreenList,
                showBadges = listOf(false, state.hasUnReadMessages, false),
                currentDestination = navBackStackEntry?.destination,
                onNavigateTo = { viewModel.sendEffect(HomeEffect.NavigateToScreen(it)) },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // TODO 发送车牌
                }
            ) {
                Icon(Icons.Filled.Add, "add student menu")
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),

        ) {
            itemsIndexed(
                items = state.rooms,
                key = { index, item ->
                    state.rooms.size - index // reverse index
                }
            ) { index, roomInfo ->
                RoomCard(
                    roomInfo = roomInfo,
                    onCopy = { roomNumber ->
                        viewModel.sendEffect(HomeEffect.CopyRoomNumber(roomNumber))
                    },
                    onJoin = {},
                    isJoined = false,
                    currentTimeMillis = state.timestampMillis
                )
            }
        }
    }
}
