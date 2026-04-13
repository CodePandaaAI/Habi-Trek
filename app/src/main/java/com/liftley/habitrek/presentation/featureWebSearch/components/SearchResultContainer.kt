package com.liftley.habitrek.presentation.featureWebSearch.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SearchResultContainer(
    modifier: Modifier = Modifier,
    isPressed: () -> Boolean,
    top: Dp = 8.dp,
    bottom: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    // sizeAnimation uses simple FloatAnimation to animate towards a target value 0.9f is "isPressed" is true and vice versa to 1f if not
    val scaleAnimation = animateFloatAsState(targetValue = if (isPressed()) 0.9f else 1f, spring())

    Box(
        modifier
            .graphicsLayer {
                scaleX = scaleAnimation.value
                scaleY = scaleAnimation.value
            }
            .clip(
                RoundedCornerShape(
                    topStart = top,
                    topEnd = top,
                    bottomStart = bottom,
                    bottomEnd = bottom
                )
            )
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
