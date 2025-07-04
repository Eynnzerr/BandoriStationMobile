package com.eynnzerr.bandoristation.ui.dialog

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.dialog_confirm
import com.eynnzerr.bandoristation.model.account.AccountSummary
import com.eynnzerr.bandoristation.ui.component.FollowerItem
import com.eynnzerr.bandoristation.utils.AppLogger
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowListDialog(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    title: String = "",
    followingIds: Set<Long>,
    followList: List<AccountSummary>,
    onFollow: (Long) -> Unit,
    placeholder: @Composable () -> Unit = {},
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(title)
            },
            text = {
                if (followList.isEmpty()) {
                    placeholder()
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        items(
                            items = followList,
                            key = { it.hashCode() }
                        ) {
                            FollowerItem(
                                avatar = it.avatar,
                                username = it.username,
                                introduction = it.introduction,
                                hasFollowed = it.userId in followingIds,
                                onFollow = { onFollow(it.userId) }
                            )

                            AppLogger.d(TAG, "id: ${it.userId}, followingIds: ${followingIds}")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text(stringResource(Res.string.dialog_confirm))
                }
            },
            dismissButton = {},
        )
    }
}

private const val TAG = "FollowListDialog"