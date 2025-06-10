package com.eynnzerr.bandoristation.feature.settings

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Filter
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.SaveAlt
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.settings_screen_title
import bandoristationm.composeapp.generated.resources.theme
import bandoristationm.composeapp.generated.resources.theme_desc
import bandoristationm.composeapp.generated.resources.settings_auto_clear_expired_rooms_title
import bandoristationm.composeapp.generated.resources.settings_auto_clear_expired_rooms_desc
import bandoristationm.composeapp.generated.resources.settings_auto_filter_pjsk_title
import bandoristationm.composeapp.generated.resources.settings_auto_filter_pjsk_desc
import bandoristationm.composeapp.generated.resources.settings_show_player_data_title
import bandoristationm.composeapp.generated.resources.settings_show_player_data_desc
import bandoristationm.composeapp.generated.resources.settings_active_filter_rules_title
import bandoristationm.composeapp.generated.resources.settings_active_filter_rules_desc
import com.eynnzerr.bandoristation.getPlatform
import com.eynnzerr.bandoristation.ui.component.app.AppTopBar
import com.eynnzerr.bandoristation.ui.component.BandThemeButton
import com.eynnzerr.bandoristation.ui.component.settings.SettingDropdownItem
import com.eynnzerr.bandoristation.ui.component.settings.SettingItem
import com.eynnzerr.bandoristation.ui.dialog.HelpDialog
import com.eynnzerr.bandoristation.ui.dialog.RegexFilterDialog
import com.eynnzerr.bandoristation.ui.ext.appBarScroll
import com.eynnzerr.bandoristation.ui.theme.bandThemeList
import com.eynnzerr.bandoristation.utils.rememberFlowWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navController: NavHostController,
    viewModel: SettingViewModel = koinViewModel<SettingViewModel>()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val effect = rememberFlowWithLifecycle(viewModel.effect)

    var showTutorialDialog by remember { mutableStateOf(false) }
    var showRegexDialog by remember { mutableStateOf(false) }

    LaunchedEffect(effect) {
        effect.collect { action ->
            when (action) {
                is SettingEffect.ControlTutorialDialog -> {
                    showTutorialDialog = action.isShowing
                }

                is SettingEffect.ControlRegexDialog -> {
                    showRegexDialog = action.isShowing
                }
            }
        }
    }

    HelpDialog(
        isVisible = showTutorialDialog,
        markdownPath = "files/introduction.md",
        onDismissRequest = { viewModel.sendEffect(SettingEffect.ControlTutorialDialog(false)) },
    )

    RegexFilterDialog(
        isVisible = showRegexDialog,
        onDismissRequest = { viewModel.sendEffect(SettingEffect.ControlRegexDialog(false)) },
        onConfirm = {}
    )

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier.appBarScroll(true, scrollBehavior),
        topBar = {
            AppTopBar(
                title = stringResource(Res.string.settings_screen_title),
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = ""
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            SettingItem(
                title = stringResource(Res.string.settings_auto_clear_expired_rooms_title),
                desc = stringResource(Res.string.settings_auto_clear_expired_rooms_desc),
                icon = Icons.Outlined.Schedule,
                action = {
                    Switch(
                        checked = state.isClearingOutdatedRoom,
                        onCheckedChange = { viewModel.sendEvent(SettingEvent.UpdateClearOutdatedRoom(it)) }
                    )
                },
                onClick = {}
            )

            SettingItem(
                title = stringResource(Res.string.settings_auto_filter_pjsk_title),
                desc = stringResource(Res.string.settings_auto_filter_pjsk_desc),
                icon = Icons.Outlined.Block,
                action = {
                    Switch(
                        checked = state.isFilteringPJSK,
                        onCheckedChange = { viewModel.sendEvent(SettingEvent.UpdateFilterPJSK(it)) }
                    )
                },
                onClick = {}
            )

            SettingItem(
                title = stringResource(Res.string.settings_show_player_data_title),
                desc = stringResource(Res.string.settings_show_player_data_desc),
                icon = Icons.Outlined.SaveAlt,
                action = {
                    Switch(
                        checked = state.isRecordingRoomHistory,
                        onCheckedChange = { viewModel.sendEvent(SettingEvent.UpdateRecordRoomHistory(it)) }
                    )
                },
                onClick = {}
            )

            var intervalText by remember(state.autoUploadInterval) { mutableStateOf(state.autoUploadInterval.toString()) }
            SettingItem(
                title = "持续发车间隔(秒)",
                desc = "自动上传房间的时间间隔(>8秒)",
                icon = Icons.Outlined.Schedule,
                action = {
                    OutlinedTextField(
                        value = intervalText,
                        onValueChange = {
                            intervalText = it.filter { ch -> ch.isDigit() }
                            intervalText.toLongOrNull()?.let { v ->
                                if (v >= 8) {
                                    viewModel.sendEvent(SettingEvent.UpdateAutoUploadInterval(v))
                                }
                            }
                        },
                        modifier = Modifier.width(80.dp),
                        singleLine = true,
                        isError = intervalText.toLongOrNull()?.let { interval -> interval > 8 } ?: false
                    )
                },
                onClick = {}
            )

            SettingDropdownItem(
                title = stringResource(Res.string.theme),
                desc = stringResource(Res.string.theme_desc),
                icon = Icons.Outlined.Palette,
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    bandThemeList.forEach {
                        BandThemeButton(
                            bandTheme = it,
                            selected = it.name == state.themeName,
                            onSelect = { viewModel.sendEvent(SettingEvent.UpdateBandTheme(it.name)) }
                        )
                    }
                }
            }

//            SettingItem(
//                title = stringResource(Res.string.settings_active_filter_rules_title),
//                desc = stringResource(Res.string.settings_active_filter_rules_desc),
//                icon = Icons.Outlined.FilterList,
//                // onClick = { viewModel.sendEffect(SettingEffect.ControlRegexDialog(true)) },
//                onClick = {},
//                enable = false
//            )

            SettingItem(
                title = "术语教程",
                desc = "关于车头常用术语的简易教程",
                icon = Icons.Outlined.School,
                onClick = { viewModel.sendEffect(SettingEffect.ControlTutorialDialog(true)) }
            )

            SettingItem(
                title = "版本",
                desc = state.versionName,
                icon = Icons.Outlined.Update,
                onClick = {
                    // TODO 检查更新
                }
            )
        }
    }
}