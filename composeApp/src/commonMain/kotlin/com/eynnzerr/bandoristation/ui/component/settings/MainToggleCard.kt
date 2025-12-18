package com.eynnzerr.bandoristation.ui.component.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eynnzerr.bandoristation.ui.theme.BandoriTheme

@Composable
fun MainCard(
    action: @Composable () -> Unit = {},
    onClick: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                enabled = true,
            ),
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Login,
                contentDescription = "",
                modifier = Modifier.padding(end = 16.dp),
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "启用扩展服务",
                    style = MaterialTheme.typography.titleLargeEmphasized.copy(fontSize = 20.sp),
                )

                Text(
                    text = "车牌加密、群组聊天",
                    style = MaterialTheme.typography.bodyMediumEmphasized,
                )
            }

            action()
        }
    }
}

@Preview
@Composable
fun MainCardPreview() {
    BandoriTheme {
        MainCard(
            action = {
                Text(
                    text = "30天",
                    color = MaterialTheme.colorScheme.error
                )
            }
        ) {}
    }
}