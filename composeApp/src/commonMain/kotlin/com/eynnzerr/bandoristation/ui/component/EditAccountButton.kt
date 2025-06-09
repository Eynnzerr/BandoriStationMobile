package com.eynnzerr.bandoristation.ui.component

import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.edit_profile
import bandoristationm.composeapp.generated.resources.login_or_register
import org.jetbrains.compose.resources.stringResource

@Composable
fun EditAccountButton(
    isLoggedIn: Boolean,
    onLogIn: () -> Unit,
    onEditAccount: () -> Unit,
) {
    Button(
        onClick = { if (isLoggedIn) onEditAccount() else onLogIn() },
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            if (isLoggedIn) {
                stringResource(Res.string.edit_profile)
            } else {
                stringResource(Res.string.login_or_register)
            }
        )
    }
}