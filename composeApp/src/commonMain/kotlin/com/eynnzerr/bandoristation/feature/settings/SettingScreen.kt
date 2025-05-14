package com.eynnzerr.bandoristation.feature.settings

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PictureInPicture
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.settings_screen_title
import bandoristationm.composeapp.generated.resources.theme
import bandoristationm.composeapp.generated.resources.theme_desc
import com.eynnzerr.bandoristation.ui.component.AppTopBar
import com.eynnzerr.bandoristation.ui.component.BandThemeButton
import com.eynnzerr.bandoristation.ui.component.settings.SettingDropdownItem
import com.eynnzerr.bandoristation.ui.component.settings.SettingItem
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

    // val appProperty = LocalAppProperty.current
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
                title = "画中画",
                desc = "管理悬浮窗组件配置",
                icon = Icons.Outlined.PictureInPicture,
                action = {
                    Switch(
                        checked = false,
                        onCheckedChange = null
                    )
                },
                onClick = {},
                enable = false,
            )

            SettingItem(
                title = "自动过滤PJSK",
                desc = "自动屏蔽PJSK的车（车牌少于六位数）",
                icon = Icons.Outlined.Block,
                action = {
                    Switch(
                        checked = true,
                        onCheckedChange = { viewModel.sendEvent(SettingEvent.UpdateFilterPJSK(it)) }
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
        }
    }
}