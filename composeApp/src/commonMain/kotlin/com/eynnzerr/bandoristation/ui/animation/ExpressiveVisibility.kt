package com.eynnzerr.bandoristation.ui.animation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin

@Composable
fun ExpressiveVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    transformOrigin: TransformOrigin = TransformOrigin.Center,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    // 定义入场动画：淡入 + 弹性缩放
    val enterTransition = fadeIn(
        animationSpec = tween(durationMillis = 200)
    ) + scaleIn(
        // 初始大小设为 0.8f 比 0.0f 看起来更自然，不会显得太突兀
        initialScale = 0.8f,
        transformOrigin = transformOrigin,
        // 关键：使用 Spring 实现“Q弹”的感觉
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy, // 低阻尼，产生回弹
            stiffness = Spring.StiffnessMediumLow
        )
    )

    // 定义出场动画：淡出 + 快速收缩
    val exitTransition = fadeOut(
        animationSpec = tween(durationMillis = 150)
    ) + scaleOut(
        targetScale = 0.8f,
        transformOrigin = transformOrigin,
        // 出场通常不需要回弹，使用标准的缓动曲线更干净
        animationSpec = tween(
            durationMillis = 150,
            easing = FastOutLinearInEasing
        )
    )

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = enterTransition,
        exit = exitTransition,
        content = content
    )
}
