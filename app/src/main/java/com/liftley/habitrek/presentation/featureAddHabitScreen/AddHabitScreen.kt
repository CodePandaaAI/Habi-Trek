package com.liftley.habitrek.presentation.featureAddHabitScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.liftley.habitrek.R
import com.liftley.habitrek.core.ui.components.HabiTrekSurface
import com.liftley.habitrek.core.ui.components.HabiTrekSectionThubnail
import com.liftley.habitrek.presentation.featureAddHabitScreen.components.ColorBall
import com.liftley.habitrek.presentation.featureAddHabitScreen.components.HabitCardPreview
import com.liftley.habitrek.presentation.util.toDurationString

@Composable
fun AddHabitScreen(onAddHabitClick: () -> Unit) {
    val addHabitViewModel = hiltViewModel<AddHabitViewModel>()
    val state by addHabitViewModel.state.collectAsState()

    val newHabitName = state.habitUiModel.name
    val habitPalette = state.habitPalette
    val newHabitColor = state.habitUiModel.color
    val newHabitDurationMinutes = state.habitUiModel.durationMinutes

    val habitDurationInHoursAndMinutes = newHabitDurationMinutes.toDurationString()

    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HabiTrekSurface {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HabiTrekSectionThubnail(
                        contentDescription = "Habit Name",
                        imageVector = painterResource(R.drawable.outline_heart_smile_24),
                        color = if (state.habitUiModel.color == Color(0L)) MaterialTheme.colorScheme.primary else
                            newHabitColor

                    )
                    Text(
                        "Name your habit",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                OutlinedTextField(
                    value = newHabitName,
                    onValueChange = { addHabitViewModel.onHabitNameChange(it) },
                    label = {
                        Text(
                            text = "What's the habit?",
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    placeholder = {
                        Text(
                            "e.g. Morning Yoga",
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    textStyle = MaterialTheme.typography.titleLarge,
                )
            }
        }

        HabiTrekSurface {
            Column(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HabiTrekSectionThubnail(
                        contentDescription = "Allocated Time",
                        imageVector = painterResource(R.drawable.outline_access_time_24),
                        color = if (state.habitUiModel.color == Color(0L)) MaterialTheme.colorScheme.primary else
                            newHabitColor

                    )
                    Text(
                        "Allocated Time?",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = if (newHabitDurationMinutes == 0) "" else newHabitDurationMinutes.toString(),
                        onValueChange = { newValue ->
                            addHabitViewModel.onDurationChange(newValue)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                        placeholder = { Text("time in mins") },
                        maxLines = 1,

                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .height(56.dp)
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(newHabitColor.copy(0.1f))
                    ) {
                        Text(
                            habitDurationInHoursAndMinutes,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }

        HabiTrekSurface {
            Column(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HabiTrekSectionThubnail(
                        contentDescription = "Color Selector",
                        imageVector = painterResource(R.drawable.baseline_color_theme_24),
                        color = if (state.habitUiModel.color == Color(0L)) MaterialTheme.colorScheme.primary else
                            newHabitColor
                    )
                    Text(
                        "Choose a color",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    habitPalette.forEachIndexed { index, colorLong ->
                        ColorBall(colorLong, state.habitUiModel.color == Color(colorLong)) {
                            addHabitViewModel.onColorChange(index)
                        }
                    }
                }

                Button(
                    onClick = { addHabitViewModel.onColorChange(0) },
                    Modifier.fillMaxWidth()
                ) {
                    Text("Use System Default Color Scheme")
                }
            }
        }

        HabiTrekSurface {
            Column(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Preview",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                HabitCardPreview(
                    habitName = newHabitName,
                    habitDurationMinutes = newHabitDurationMinutes,
                    surfaceColor = if (state.habitUiModel.color == Color(0L)) MaterialTheme.colorScheme.primary else
                        newHabitColor,
                    textColor = if (state.habitUiModel.color == Color(0L)) MaterialTheme.colorScheme.onPrimary else Color.Black
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            enabled = !newHabitName.isBlank() && newHabitDurationMinutes > 0,
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            shape = RoundedCornerShape(48.dp),
            onClick = {
                addHabitViewModel.createHabit(onAddHabitClick)
            }
        ) {
            Text(
                "Create Habit",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddHabitScreenPreview() {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Add a Habit", style = MaterialTheme.typography.headlineLarge)

        BasicTextField(state = TextFieldState(initialText = "Learn Android Dev"))
    }
}
