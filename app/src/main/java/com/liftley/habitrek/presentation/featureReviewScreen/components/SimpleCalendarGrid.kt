package com.liftley.habitrek.presentation.featureReviewScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.YearMonth
import java.time.ZoneOffset

@Composable
fun SimpleCalendarGrid(
    currentMonth: YearMonth,
    completedDates: Set<Long>,
    habitColor: Color,
    onDayClick: (Long) -> Unit
) {
    val isLight = habitColor.luminance() > 0.6f
    val textColor =
        if (isLight && !isSystemInDarkTheme()) MaterialTheme.colorScheme.onSurface
        else MaterialTheme.colorScheme.surface

    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value
    val emptyBoxesBefore = firstDayOfWeek - 1
    val totalBoxes = emptyBoxesBefore + daysInMonth
    val totalRows = (totalBoxes + 6) / 7

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Aesthetic Touch: Weekday headers ("M", "T", "W"...)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        for (row in 0 until totalRows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Ensures padding between each day
            ) {
                for (col in 0..6) {
                    val boxIndex = (row * 7) + col

                    if (boxIndex !in emptyBoxesBefore..<totalBoxes) {
                        // Invisible spacer fixes the 1f weight weird spacing bounds
                        Box(
                            modifier = Modifier
                                .weight(1f)
                        )
                    } else {
                        val dayNumber = boxIndex - emptyBoxesBefore + 1
                        val date = currentMonth.atDay(dayNumber)
                        val dateMillis =
                            date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

                        val isCompleted = completedDates.contains(dateMillis)

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .weight(1f)
                                .background(if (isCompleted) habitColor else MaterialTheme.colorScheme.surfaceContainer)
                                .clickable { onDayClick(dateMillis) }, // Tell ViewModel!
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayNumber.toString(),
                                color = textColor,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
