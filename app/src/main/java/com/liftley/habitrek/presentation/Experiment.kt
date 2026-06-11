package com.liftley.habitrek.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.liftley.habitrek.core.theme.HabiTrekExpressiveTheme
import kotlin.math.PI
import kotlin.math.atan2

@Preview(showBackground = true, name = "TimeWheel")
@Composable
fun TimeWheelPreview() {
    HabiTrekExpressiveTheme {
        var rotationAngle by remember { mutableFloatStateOf(0f) }
        val textMeasurer = rememberTextMeasurer()
        val circleColor = MaterialTheme.colorScheme.surface
        val lineCount = 1440
        val angleStep = 360f / lineCount
        val textStyle = TextStyle(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .size(300.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            val centerX = size.width / 2f
                            val centerY = size.height / 2f
                            val x = change.position.x - centerX
                            val y = change.position.y - centerY
                            val prevX = change.previousPosition.x - centerX
                            val prevY = change.previousPosition.y - centerY

                            val currentAngle = atan2(y, x) * (180 / PI).toFloat()
                            val previousAngle = atan2(prevY, prevX) * (180 / PI).toFloat()

                            rotationAngle += (currentAngle - previousAngle)
                        }
                    }
            ) {
                val radius = size.minDimension / 2
                val center = Offset(size.width / 2, size.height / 2)

                drawCircle(
                    color = circleColor,
                    radius = radius,
                    center = center
                )

                rotate(rotationAngle, pivot = center) {
                    for (i in 1..lineCount) {
                        // Only draw every 10th or 60th line to maintain performance/readability
                        // if 1440 lines is too dense, but logic for all 1440 is here:
                        if (i % 5 == 0) { // Drawing every 5th minute for visual clarity
                            val angleInDegrees = i * angleStep
                            rotate(angleInDegrees, pivot = center) {
                                val lineStart = center.copy(y = center.y - radius + 10.dp.toPx())
                                val lineEnd = center.copy(y = center.y - radius + 30.dp.toPx())

                                drawLine(
                                    color = Color.Gray,
                                    start = lineStart,
                                    end = lineEnd,
                                    strokeWidth = 1.dp.toPx()
                                )

                                val textLayoutResult = textMeasurer.measure("$i min", style = textStyle)
                                drawText(
                                    textLayoutResult = textLayoutResult,
                                    topLeft = Offset(
                                        center.x - textLayoutResult.size.width / 2,
                                        center.y - radius + 35.dp.toPx()
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}