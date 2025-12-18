package com.eynnzerr.bandoristation.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TooltipScope
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAvatar(
    avatarName: String,
    size: Dp = 32.dp,
    shape: Shape = CircleShape,
    modifier: Modifier = Modifier,
    tooltipContent: @Composable (TooltipScope.() -> Unit),
) {
    val state = rememberTooltipState(isPersistent = true)
    val scope = rememberCoroutineScope()

    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.End),
        tooltip = tooltipContent,
        hasAction = false,
        state = state,
    ) {
        UserAvatar(
            avatarName = avatarName,
            size = size,
            shape = shape,
            modifier = modifier,
            onClick = {
                scope.launch {
                    state.show()
                }
            }
        )
    }
}

@Preview
@Composable
private fun UserAvatarPreview() {
    val avatarName = "c2faea61dafc38e8ca2b6de329289d81.png"
    UserAvatar(avatarName)
}