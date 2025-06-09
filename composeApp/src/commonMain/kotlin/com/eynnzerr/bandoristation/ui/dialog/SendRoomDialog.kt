package com.eynnzerr.bandoristation.ui.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.eynnzerr.bandoristation.model.RoomUploadInfo

/**
 * 房间信息对话框组件
 *
 * @param isVisible 控制对话框是否可见
 * @param onDismissRequest 当用户请求关闭对话框时的回调
 * @param onSendClick 当用户点击发送按钮时的回调，传递房间号、描述和预选词列表
 */
@Composable
fun SendRoomDialog(
    isVisible: Boolean,
    presetWords: Set<String>,
    onDismissRequest: () -> Unit,
    onSendClick: (uploadInfo: RoomUploadInfo, continuous: Boolean) -> Unit,
    onAddPresetWord: (String) -> Unit,
    onDeletePresetWord: (String) -> Unit,
    prefillRoomNumber: String = "",
    prefillDescription: String = "",
) {
    if (isVisible) {
        var roomNumber by remember(prefillRoomNumber) { mutableStateOf(prefillRoomNumber) }
        var description by remember(prefillDescription) { mutableStateOf(prefillDescription) }
        var newPresetWord by remember { mutableStateOf("") }
        var continuous by remember { mutableStateOf(false) }
        var isPresetWordsExpanded by remember { mutableStateOf(false) }
        var isAddWordsExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = onDismissRequest,
            icon = {
                Icon(
                    imageVector = Icons.Default.Publish,
                    contentDescription = "send room"
                )
            },
            title = { Text("上传房间信息") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 房间号输入框
                    OutlinedTextField(
                        value = roomNumber,
                        onValueChange = { roomNumber = it },
                        label = { Text("房间号") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { roomNumber = "" }) {
                                Icon(Icons.Filled.Clear, contentDescription = "清空")
                            }
                        }
                    )

                    // 描述输入框
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("描述") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4,
                        trailingIcon = {
                            IconButton(onClick = { description = "" }) {
                                Icon(Icons.Filled.Clear, contentDescription = "清空")
                            }
                        }
                    )

                    // 预选词部分
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { isPresetWordsExpanded = !isPresetWordsExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "预选词",
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Icon(
                                if (isPresetWordsExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                contentDescription = if (isPresetWordsExpanded) "收起" else "展开"
                            )
                        }

                        AnimatedVisibility(
                            visible = isPresetWordsExpanded,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // 预选词列表
                                Row(
                                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    FilledTonalIconToggleButton(
                                        checked = isAddWordsExpanded,
                                        onCheckedChange = {
                                            isAddWordsExpanded = !isAddWordsExpanded
                                        },
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "add preset words"
                                        )
                                    }

                                    if (presetWords.isEmpty()) {
                                        Text("暂无预选词。")
                                    }

                                    presetWords.forEach { word ->
                                        InputChip(
                                            selected = false,
                                            onClick = {
                                                description += word
                                            },
                                            label = { Text(word) },
                                            trailingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = null,
                                                    modifier = Modifier.clickable { onDeletePresetWord(word) }
                                                )
                                            }
                                        )
                                    }
                                }

                                // 新增预选词
                                AnimatedVisibility(
                                    visible = isAddWordsExpanded,
                                    enter = expandVertically(),
                                    exit = shrinkVertically()
                                ) {
                                    OutlinedTextField(
                                        value = newPresetWord,
                                        onValueChange = { newPresetWord = it },
                                        label = { Text("新预选词") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        trailingIcon = {
                                            TextButton(
                                                onClick = {
                                                    if (newPresetWord.isNotBlank()) {
                                                        onAddPresetWord(newPresetWord)
                                                        newPresetWord = ""
                                                    }
                                                },
                                            ) {
                                                Text("添加")
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = continuous,
                        onCheckedChange = { continuous = it }
                    )
                    Text("持续发送车牌直到下次发车")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (roomNumber.isNotBlank()) {
                            val roomInfo = RoomUploadInfo(
                                number = roomNumber,
                                description = description
                            )
                            onSendClick(roomInfo, continuous)
                            onDismissRequest()
                        }
                    }
                ) {
                    Text("发送")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text("取消")
                }
            }
        )
    }
}
