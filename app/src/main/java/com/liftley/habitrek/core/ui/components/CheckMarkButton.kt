package com.liftley.habitrek.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CheckMarkButton(
    isHabitCompletedToday: () -> Boolean,
    actualElementSize: Dp,
    iconsSize: Dp,
    habitColor: () -> Color,
    onClickListener: () -> Unit
) {
    val isHabitCompletedToday = isHabitCompletedToday()
    val habitColor = habitColor()
    /*
    Variables for Simple scaleX and scaleY animation for shrinking and expanding of CheckMark Button
     */
    // Mutable State "var isPressed" for checking if CheckMarkButton is clicked or not for running shrink/expand animation
    var isPressed by remember { mutableStateOf(false) }

    // sizeAnimation uses simple FloatAnimation to animate towards a target value 0.8f is "isPressed" is true and vice versa to 1f if not
    val scaleAnimation = animateFloatAsState(targetValue = if (isPressed) 0.8f else 1f, spring())

    // After Shrink/Expand animation and proper tap, decides color of CheckMarkButton
    val toggleColor by animateColorAsState(
        targetValue =
            if (isHabitCompletedToday) habitColor
            else MaterialTheme.colorScheme.surface,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "HeroToggleColor"
    )
    Box(
        Modifier
            .graphicsLayer {
                scaleX = scaleAnimation.value
                scaleY = scaleAnimation.value
            }
            .size(actualElementSize)
            .clip(CircleShape)
            .border(2.dp, habitColor, shape = CircleShape)
            .background(color = toggleColor)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        // 1. FINGER DOWN: Trigger the shrink animation immediately
                        isPressed = true

                        // 2. WAIT: Pause execution and see how the tap resolves
                        tryAwaitRelease()

                        // 3. FINGER UP OR CANCELED: Trigger the release animation
                        // Notice this happens whether the tap succeeded or was stolen! Let it bounce back!
                        isPressed = false
                    },
                    onTap = {
                        onClickListener()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        if (isHabitCompletedToday) {
            Icon(
                imageVector = Icons.Default.Check,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(iconsSize),
                contentDescription = "Decoration"
            )
        } else {
            Icon(
                imageVector = Icons.Default.Check,
                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                modifier = Modifier.size(iconsSize),
                contentDescription = "Decoration"
            )
        }
    }
}