package com.liftley.habitrek.presentation.featureAddHabitScreen.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.liftley.habitrek.core.designSystem.theme.LiftleyTheme
import com.liftley.habitrek.presentation.util.toDurationString

@Composable
fun HabitCardPreview(
    habitName: String,
    habitDurationMinutes: Int,
    surfaceColor: Color,
    textColor: Color
) {
    val hoursAndMinutes = habitDurationMinutes.toDurationString()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = surfaceColor
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
                    text = habitName.ifBlank { "Morning Yoga" },
                    style = MaterialTheme.typography.titleLarge,
                    color = textColor,
                    modifier = Modifier
                )
                Text(
                    hoursAndMinutes,
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HabitCardPreviewP() {
    LiftleyTheme {
        HabitCardPreview(
            habitName = "",
            habitDurationMinutes = 120,
            surfaceColor = MaterialTheme.colorScheme.primary,
            textColor = MaterialTheme.colorScheme.onPrimary,
        )
    }
}