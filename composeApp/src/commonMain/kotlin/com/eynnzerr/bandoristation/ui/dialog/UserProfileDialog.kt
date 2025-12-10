package com.eynnzerr.bandoristation.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.user_profile_dialog_close_button_desc
import bandoristationm.composeapp.generated.resources.user_profile_dialog_follow_button
import bandoristationm.composeapp.generated.resources.user_profile_dialog_unfollow_button
import com.eynnzerr.bandoristation.model.account.AccountInfo
import com.eynnzerr.bandoristation.ui.component.OtherUserProfile
import org.jetbrains.compose.resources.stringResource

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
                Box(
                    contentAlignment = Alignment.TopCenter
                ) {
                    OtherUserProfile(
                        accountInfo = accountInfo,
                        sideButton = {
                            Button(
                                onClick = { onFollow(accountInfo.accountSummary.userId) },
                                shape = MaterialTheme.shapes.medium,
                            ) {
                                Text(stringResource(if (hasFollowed) Res.string.user_profile_dialog_unfollow_button else Res.string.user_profile_dialog_follow_button))
                            }
                        },
                        actionButton = {
                            FilledTonalIconButton(
                                onClick = onDismissRequest
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Cancel,
                                    contentDescription = stringResource(Res.string.user_profile_dialog_close_button_desc)
                                )
                            }
                        }
                    )

                    if (accountInfo.accountSummary.userId == 0L) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(Color.Black.copy(alpha = 0.5f))
                        )

                        LoadingIndicator()
                    }
                }
            }
        }
    }
}
