package com.eynnzerr.bandoristation.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import bandoristationm.composeapp.generated.resources.Res
import coil3.compose.AsyncImage
import com.eynnzerr.bandoristation.ui.theme.BandThemeConfig
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(InternalResourceApi::class)
@Composable
fun BandThemeButton(
    bandTheme: BandThemeConfig,
    selected: Boolean,
    size: Dp = 48.dp,
    onSelect: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.clickable(onClick = onSelect)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .background(Color(bandTheme.seedColorLong), CircleShape)
        )

        bandTheme.bandIcon?.let {
            Image(
                painter = painterResource(it),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }

        if (selected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
        }
    }
}