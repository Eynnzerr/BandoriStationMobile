package com.eynnzerr.bandoristation.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialShapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.default_avatar
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.eynnzerr.bandoristation.data.remote.NetworkUrl
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun UserAvatar(
    avatarName: String,
    size: Dp = 32.dp,
    shape: Shape = CircleShape,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {

    if (avatarName == "") {
        Image(
            painter = painterResource(Res.drawable.default_avatar),
            contentDescription = "uploader avatar",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(shape)
                .clickable(onClick = onClick),
        )
    } else {
        AsyncImage(
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(NetworkUrl.AVATAR + avatarName)
                .crossfade(true)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build(),
            placeholder = painterResource(Res.drawable.default_avatar),
            error = painterResource(Res.drawable.default_avatar),
            fallback = painterResource(Res.drawable.default_avatar),
            contentDescription = "uploader avatar",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(shape)
                .clickable(onClick = onClick),
        )
    }
}

@Preview
@Composable
private fun UserAvatarPreview() {
    val avatarName = "c2faea61dafc38e8ca2b6de329289d81.png"
    UserAvatar(avatarName)
}