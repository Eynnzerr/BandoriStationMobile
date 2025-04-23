package com.eynnzerr.bandoristation.ui.dialog

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eynnzerr.bandoristation.model.account.AccountInfo
import com.eynnzerr.bandoristation.ui.component.UserProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileDialog(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    accountInfo: AccountInfo,
    hasFollowed: Boolean = false,
    onFollow: (Long) -> Unit = {},
) {
    if (isVisible) {
        BasicAlertDialog(
            onDismissRequest = onDismissRequest,
            modifier = Modifier.padding(vertical = 32.dp)
        ) {
            Surface(
                modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                UserProfile(
                    accountInfo = accountInfo,
                    sideButton = {
                        Button(
                            onClick = { onFollow(accountInfo.accountSummary.userId) },
                            shape = MaterialTheme.shapes.medium,
                        ) {
                            Text(if (hasFollowed) "取关" else "关注")
                        }
                    },
                    actionButton = {
                        FilledTonalIconButton(
                            onClick = onDismissRequest
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Cancel,
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        }
    }
}