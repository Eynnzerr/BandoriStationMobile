package com.eynnzerr.bandoristation.ui.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eynnzerr.bandoristation.model.RoomFilter
import com.eynnzerr.bandoristation.model.UserInfo
import com.eynnzerr.bandoristation.utils.AppLogger
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun RoomFilterDialog(
    isVisible: Boolean,
    presetWords: List<String>,
    presetUsers: List<UserInfo>,
    onDismissRequest: () -> Unit,
    onConfirm: (RoomFilter) -> Unit,
) {
    val initialWords by remember(presetWords) { mutableStateOf(presetWords) }
    val initialUsers by remember(presetUsers) { mutableStateOf(presetUsers) }

    var keywordText by remember { mutableStateOf("") }
    val filterWords = remember(presetWords) { mutableStateListOf(*presetWords.toTypedArray()) }
    val filterUsers = remember(presetUsers) { mutableStateListOf(*presetUsers.toTypedArray()) }

    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            icon = {
                Icon(
                    imageVector = Icons.Default.FilterAlt,
                    contentDescription = null,
                )
            },
            title = {
                Text("房间筛选")
            },
            text = {
                Column {
                    // 屏蔽关键词部分
                    Text(
                        text = "屏蔽关键词",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // 关键词输入框和添加按钮
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = keywordText,
                            onValueChange = { keywordText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("关键词") },
                            shape = RoundedCornerShape(4.dp),
                            trailingIcon = {
                                IconButton(onClick = {}) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "编辑"
                                    )
                                }
                            }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (keywordText.isNotEmpty()) {
                                    filterWords.add(keywordText)
                                    keywordText = ""
                                }
                            },
                        ) {
                            Text("添加")
                        }
                    }

                    // 已添加的关键词
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        filterWords.forEach { keyword ->
                            InputChip(
                                selected = false,
                                onClick = {},
                                label = { Text(keyword) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = null,
                                        modifier = Modifier.clickable { filterWords.remove(keyword) }
                                    )
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 屏蔽用户部分
                    Text(
                        text = "屏蔽用户",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // 已屏蔽的用户
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        filterUsers.forEach { user ->
                            InputChip(
                                selected = false,
                                onClick = {},
                                label = { Text(user.username ?: "") },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = null,
                                        modifier = Modifier.clickable { filterUsers.remove(user) }
                                    )
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val filter = RoomFilter(
                            keyword = filterWords,
                            user = filterUsers,
                        )
                        onConfirm(filter)
                        onDismissRequest()
                    }
                ) {
                    Text("应用")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        keywordText = ""
                        filterWords.apply {
                            clear()
                            addAll(initialWords)
                        }
                        filterUsers.apply {
                            clear()
                            addAll(initialUsers)
                        }
                    }
                ) {
                    Text("重置")
                }
            },
        )
    }
}

@Preview
@Composable
fun RoomFilterDialogPreview() {
    RoomFilterDialog(
        isVisible = true,
        presetWords = listOf("关键词1", "关键词2"),
        presetUsers = listOf(UserInfo(username = "用户1"), UserInfo(username = "用户2")),
        onDismissRequest = {},
        onConfirm = {}
    )
}

private const val TAG = "RoomFilterDialog"