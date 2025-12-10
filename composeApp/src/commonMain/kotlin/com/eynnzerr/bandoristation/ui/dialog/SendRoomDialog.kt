package com.eynnzerr.bandoristation.ui.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.send_room_dialog_add_button
import bandoristationm.composeapp.generated.resources.send_room_dialog_add_preset_words_icon_desc
import bandoristationm.composeapp.generated.resources.send_room_dialog_cancel_button
import bandoristationm.composeapp.generated.resources.send_room_dialog_clear_icon_desc
import bandoristationm.composeapp.generated.resources.send_room_dialog_collapse_icon_desc
import bandoristationm.composeapp.generated.resources.send_room_dialog_continuous_send_checkbox
import bandoristationm.composeapp.generated.resources.send_room_dialog_description_label
import bandoristationm.composeapp.generated.resources.send_room_dialog_encrypt_checkbox
import bandoristationm.composeapp.generated.resources.send_room_dialog_expand_icon_desc
import bandoristationm.composeapp.generated.resources.send_room_dialog_icon_desc
import bandoristationm.composeapp.generated.resources.send_room_dialog_new_preset_word_label
import bandoristationm.composeapp.generated.resources.send_room_dialog_no_preset_words
import bandoristationm.composeapp.generated.resources.send_room_dialog_preset_words_button
import bandoristationm.composeapp.generated.resources.send_room_dialog_room_number_error
import bandoristationm.composeapp.generated.resources.send_room_dialog_room_number_label
import bandoristationm.composeapp.generated.resources.send_room_dialog_send_button
import bandoristationm.composeapp.generated.resources.send_room_dialog_title
import com.eynnzerr.bandoristation.utils.AppLogger
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

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
    onSendClick: (number: String, desc: String, continuous: Boolean, encrypted: Boolean) -> Unit,
    onAddPresetWord: (String) -> Unit,
    onDeletePresetWord: (String) -> Unit,
    prefillRoomNumber: String = "",
    prefillDescription: String = "",
) {
    if (isVisible) {
        var roomNumber by remember(prefillRoomNumber) { mutableStateOf(prefillRoomNumber) }
        var description by remember(prefillDescription) { mutableStateOf(TextFieldValue(prefillDescription)) }
        var newPresetWord by remember { mutableStateOf("") }
        var isPresetWordsExpanded by remember { mutableStateOf(false) }
        var continuous by remember { mutableStateOf(false) }
        var isEncrypted by remember { mutableStateOf(false) }
        var isAddWordsExpanded by remember { mutableStateOf(false) }
        var isRoomNumberError by remember { mutableStateOf(false) }
        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current

        AlertDialog(
            onDismissRequest = onDismissRequest,
            icon = {
                Icon(
                    imageVector = Icons.Default.Publish,
                    contentDescription = stringResource(Res.string.send_room_dialog_icon_desc)
                )
            },
            title = { Text(stringResource(Res.string.send_room_dialog_title)) },
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
                        onValueChange = {
                            val newText = it.filter { char -> char.isDigit() }
                            if (newText.length <= 6) {
                                roomNumber = newText
                            }
                            isRoomNumberError = newText.length != 6
                        },
                        label = { Text(stringResource(Res.string.send_room_dialog_room_number_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { roomNumber = "" }) {
                                Icon(Icons.Filled.Clear, contentDescription = stringResource(Res.string.send_room_dialog_clear_icon_desc))
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusRequester.requestFocus()
                            }
                        ),
                        isError = isRoomNumberError,
                        supportingText = {
                            if (isRoomNumberError) {
                                Text(stringResource(Res.string.send_room_dialog_room_number_error))
                            }
                        }
                    )

                    // 描述输入框
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(stringResource(Res.string.send_room_dialog_description_label)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        minLines = 2,
                        maxLines = 4,
                        trailingIcon = {
                            IconButton(onClick = { description = TextFieldValue("") }) {
                                Icon(Icons.Filled.Clear, contentDescription = stringResource(Res.string.send_room_dialog_clear_icon_desc))
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
//                        keyboardActions = KeyboardActions(
//                            onDone = {
//                                // keyboardController?.hide()
//                                focusManager.clearFocus()
//                                defaultKeyboardAction(ImeAction.Done)
//                            }
//                        )
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
                                text = stringResource(Res.string.send_room_dialog_preset_words_button),
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Icon(
                                if (isPresetWordsExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                contentDescription = if (isPresetWordsExpanded) stringResource(Res.string.send_room_dialog_collapse_icon_desc) else stringResource(Res.string.send_room_dialog_expand_icon_desc)
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
                                            contentDescription = stringResource(Res.string.send_room_dialog_add_preset_words_icon_desc)
                                        )
                                    }

                                    if (presetWords.isEmpty()) {
                                        Text(stringResource(Res.string.send_room_dialog_no_preset_words))
                                    }

                                    presetWords.forEach { word ->
                                        InputChip(
                                            selected = false,
                                            onClick = {
                                                val newText = description.text + word
                                                description = TextFieldValue(
                                                    text = newText,
                                                    selection = TextRange(newText.length)
                                                )
                                                focusRequester.requestFocus()
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
                                        label = { Text(stringResource(Res.string.send_room_dialog_new_preset_word_label)) },
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
                                                Text(stringResource(Res.string.send_room_dialog_add_button))
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = continuous,
                                onCheckedChange = { continuous = it }
                            )
                            Text(stringResource(Res.string.send_room_dialog_continuous_send_checkbox))
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isEncrypted,
                                onCheckedChange = { isEncrypted = it }
                            )
                            Text(stringResource(Res.string.send_room_dialog_encrypt_checkbox))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = roomNumber.isNotBlank() && description.text.isNotBlank() && !isRoomNumberError,
                    onClick = {
                        onSendClick(roomNumber, description.text.trim(), continuous, isEncrypted)
                        onDismissRequest()
                    }
                ) {
                    Text(stringResource(Res.string.send_room_dialog_send_button))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(Res.string.send_room_dialog_cancel_button))
                }
            }
        )
    }
}

@Preview
@Composable
fun SendRoomDialogPreview() {
    val presetWords = remember { mutableStateOf(setOf("Hello", "World", "BanG Dream!")) }
    SendRoomDialog(
        isVisible = true,
        presetWords = presetWords.value,
        onDismissRequest = {  },
        onSendClick = { _, _, _ , _->  },
        onAddPresetWord = { word ->
            presetWords.value += word
        },
        onDeletePresetWord = { word ->
            presetWords.value -= word
        }
    )
}
