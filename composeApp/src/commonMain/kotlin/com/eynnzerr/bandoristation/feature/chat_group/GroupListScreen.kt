package com.eynnzerr.bandoristation.feature.chat_group

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.chat_group_list_title
import bandoristationm.composeapp.generated.resources.close_button_desc
import bandoristationm.composeapp.generated.resources.create_chat_room
import bandoristationm.composeapp.generated.resources.enter_chat_room
import com.eynnzerr.bandoristation.model.chat_group.ChatGroupDetails
import com.eynnzerr.bandoristation.model.chat_group.OwnerInfo
import com.eynnzerr.bandoristation.ui.component.chat_group.ChatGroupItem
import com.eynnzerr.bandoristation.ui.theme.BandoriTheme
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupListScreen(
    onClose: () -> Unit,
    onCreateClick: () -> Unit,
    chatGroups: List<ChatGroupDetails>,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(Res.string.chat_group_list_title)) },
                navigationIcon = {
                    IconButton(
                        onClick = onClose
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(Res.string.close_button_desc),
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onClose
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = stringResource(Res.string.enter_chat_room),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            stickyHeader(key = -1) {
                Button(
                    onClick = onCreateClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AddCircleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(stringResource(Res.string.create_chat_room))
                }
            }

            items(
                count = chatGroups.size,
                key = { index -> chatGroups[index].id },
            ) {
                ChatGroupItem(chatGroups[it])
            }
        }
    }
}

@Preview
@Composable
private fun ScreenPreview() {
    val groups = List(20) {
        ChatGroupDetails(
            id = "$it",
            name = "聊天室${it}",
            owner = OwnerInfo(
                id = "123",
                name = "test-${it}",
                avatar = ""
            ),
            memberCount = 8,
            createdAt = "2023-08-01",
            lastActivityAt = "2023-08-01"
        )
    }
    BandoriTheme {
        GroupListScreen(
            onClose = {},
            onCreateClick = {},
            chatGroups = groups,
        )
    }
}