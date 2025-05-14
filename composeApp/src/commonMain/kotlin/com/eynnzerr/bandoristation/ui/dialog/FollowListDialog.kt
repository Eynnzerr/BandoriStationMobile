package com.eynnzerr.bandoristation.ui.dialog

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eynnzerr.bandoristation.model.account.AccountSummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowListDialog(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    followList: List<AccountSummary>,
    onFollow: () -> Unit,
    onClickItem: () -> Unit,
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

            }
        }
    }
}