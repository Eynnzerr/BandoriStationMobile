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
import com.eynnzerr.bandoristation.model.chat_group.ChatGroupDetails
import com.eynnzerr.bandoristation.model.chat_group.OwnerInfo
import com.eynnzerr.bandoristation.ui.component.chat_group.ChatGroupItem
import com.eynnzerr.bandoristation.ui.theme.BandoriTheme

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
                title = { Text(text = "聊天群组一览") },
                navigationIcon = {
                    IconButton(
                        onClick = onClose
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = null,
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onClose
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
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
                    Text("创建聊天室")
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