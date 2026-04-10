package com.liftley.habitrek.presentation.featureHomeScreen.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.liftley.habitrek.core.ui.components.CheckMarkButton
import com.liftley.habitrek.presentation.util.toDurationString
import kotlinx.coroutines.launch

@Composable
fun HabitCard(
    habitName: String,
    habitDurationMinutes: Int,
    habitColor: Color,
    isCompletedToday: Boolean,
    topRounding: Dp = 8.dp,
    bottomRounding: Dp = 8.dp,
    onClickListener: () -> Unit,
    onCompletedClick: () -> Unit
) {
    /*
    Habit Related Variables for easy and fast access
    */
    val habitDurationInHoursAndMinutes = habitDurationMinutes.toDurationString() // User-friendly and Readable habit duration in hours and minutes

    /*
    3D Card Tilt Animation Related Variables containing coroutine scopes, Animatable, animateFloatAsState and mutable state values
     */
    val coroutineScope =
        rememberCoroutineScope() // Coroutine for launching Animations "animateTo" or "snapTo" from Animatable

    val rotationXAnimation =
        remember { Animatable(0f) } // Rotation X managing angle of X(Horizontal) Axis which control up or down tilts
    val rotationYAnimation =
        remember { Animatable(0f) } // Rotation Y managing angle of Y(Vertical) Axis which control left and right tilts

    Surface(
        modifier = Modifier
            .graphicsLayer {
                rotationX = rotationXAnimation.value
                rotationY = rotationYAnimation.value

                cameraDistance = 12 * density
            }
            .fillMaxWidth()
            .pointerInput(Unit) {
                val cardWidth = size.width
                val cardHeight = size.height
                val centerX = cardWidth / 2f
                val centerY = cardHeight / 2f

                // Get the system's pixel threshold for Tap vs Drag
                val touchSlop = viewConfiguration.touchSlop

                awaitEachGesture {
                    // 1. WAIT FOR TOUCH: Code pauses here until the screen is touched
                    val downEvent = awaitFirstDown()

                    // Track if this touch turns into a drag
                    var isDrag = false

                    // Trigger the initial tilt (Mimicking your old onDragStart)
                    val startDistanceFromCenterX = downEvent.position.x - centerX
                    val startDistanceFromCenterY = downEvent.position.y - centerY

                    coroutineScope.launch {
                        rotationXAnimation.animateTo(-(startDistanceFromCenterY / 24f))
                    }

                    coroutineScope.launch {
                        rotationYAnimation.animateTo(startDistanceFromCenterX / 24f)
                    }

                    // 2. TRACK MOVEMENT: Loop continuously while the finger is pressed down
                    do {
                        val event = awaitPointerEvent()
                        val change = event.changes.first() // Get the primary finger

                        if (change.isConsumed) {
                            isDrag = true // It's a scroll, so definitely don't trigger a click
                            break // Break the loop so the card stops tilting and goes flat!
                        }

                        if (change.pressed) {
                            val currentX = change.position.x
                            val currentY = change.position.y

                            // Calculate how far the finger moved from the initial touch point
                            val distanceMoved = (change.position - downEvent.position).getDistance()

                            // If they moved past the system's wiggle room, it's officially a drag
                            if (distanceMoved > touchSlop) {
                                isDrag = true
                            }

                            // Update the 3D tilt based on the finger's current location
                            coroutineScope.launch {
                                rotationXAnimation.snapTo(-(currentY - centerY) / 24f)
                            }

                            coroutineScope.launch {
                                rotationYAnimation.snapTo((currentX - centerX) / 24f)
                            }
                        }
                    } while (event.changes.any { it.pressed }) // Break the loop when all fingers lift

                    // 3. FINGER LIFTED: Reset the animations back to flat
                    coroutineScope.launch {
                        rotationXAnimation.animateTo(0f)
                    }

                    coroutineScope.launch {
                        rotationYAnimation.animateTo(0f)
                    }

                    // 4. EVALUATE: Was it a tap or a drag?
                    // If the finger never moved beyond the touchSlop threshold, fire the click listener
                    if (!isDrag) {
                        onClickListener()
                    }
                }
            },
        shape = RoundedCornerShape(
            topStart = topRounding,
            topEnd = topRounding,
            bottomStart = bottomRounding,
            bottomEnd = bottomRounding
        ),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = habitName,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                )
                Text(
                    habitDurationInHoursAndMinutes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

            }
            CheckMarkButton(
                isHabitCompletedToday = { isCompletedToday },
                actualElementSize = 48.dp,
                iconsSize = 24.dp,
                habitColor = { habitColor },
            ) {
                onCompletedClick()
            }
        }
    }
}