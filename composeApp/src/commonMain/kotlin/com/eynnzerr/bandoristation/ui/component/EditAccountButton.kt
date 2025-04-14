package com.eynnzerr.bandoristation.ui.component

import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

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
        Text(if (isLoggedIn) "编辑资料" else "登录/注册")
    }
}