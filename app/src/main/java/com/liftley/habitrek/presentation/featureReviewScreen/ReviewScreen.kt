package com.liftley.habitrek.presentation.featureReviewScreen

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.liftley.habitrek.R
import com.liftley.habitrek.core.ui.components.CheckMarkButton
import com.liftley.habitrek.core.ui.components.ExpressiveIconButton
import com.liftley.habitrek.core.ui.components.HabitContainer
import com.liftley.habitrek.core.ui.components.SectionThumbnail
import com.liftley.habitrek.presentation.featureAddHabitScreen.components.ColorBall
import com.liftley.habitrek.presentation.featureReviewScreen.components.MetricCard
import com.liftley.habitrek.presentation.featureReviewScreen.components.SimpleCalendarGrid
import com.liftley.habitrek.presentation.util.toDurationString


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(habitId: Int, onHabitDeleted: () -> Unit) {

    val keyboardController = LocalSoftwareKeyboardController.current

    /*
    Creating viewModel Instance in ReviewScreen which is scoped to
    ReviewScreen and not MainActivity in order to recycle ReviewViewModel as soon as user leaves
    the ReviewScreen.

    Passing habitId as soon as we enter Composition of this Screen.
    Hilt's own factory knows how to provide dependencies like repositories (which we defined in AppModule),
    but it doesn't know about values that only exist at runtime (like the 'habitId' passed from navigation).

    To solve this, Hilt uses "Assisted Injection":
    1. @AssistedInject: In the ViewModel, this tells Hilt that some parameters are managed by Hilt
       and some are "assisted" (provided by us later).
    2. @Assisted: Marks the 'habitId' parameter in the ViewModel constructor as the runtime value.
    3. @AssistedFactory: We create an interface inside the ViewModel. Hilt will automatically
       generate the implementation of this interface. This "Factory" acts as a builder.

    THE hiltViewModel<VM, Factory> CALL:
    - VM (ReviewViewModel): The actual class we want to use.
    - Factory (ReviewViewModel.Factory): The bridge Hilt generated.
    - hiltViewModel needs to know both so it can look up the generated factory implementation
      in Hilt's dependency graph.

    THE creationCallback:
    - This is a lambda where Hilt gives us an instance of that 'Factory' interface.
    - Inside, we call 'factory.create(habitId)'.
    - WHY Parentheses? 'Factory' is the TYPE, but inside the lambda, 'factory' (lowercase) is
      the OBJECT. We call the '.create()' method on it to finally build our ViewModel.

    UNDER THE HOOD:
    When we call factory.create(habitId), Hilt takes the 'habitId' we just gave it,
    combines it with the 'ReviewHabitRepository' it already knows how to provide,
    and calls the 'ReviewViewModel' constructor for us. This ViewModel is then stored
    in the standard ViewModelStore so it survives configuration changes!
     */
    val reviewViewModel = hiltViewModel<ReviewViewModel, ReviewViewModel.Factory>(
        creationCallback = { factory ->
            factory.create(habitId = habitId)
        }
    )

    /*
    State Variables for Delete Dialog states like isVisible and its textField which requires
    the user to write exactly "delete" word though it can be UPPERCASE or Lower Case as we at the
    end we lower case entire string so it should be just delete with any format.
     */
    var isDialogVisible by remember { mutableStateOf(false) }
    var dialogTextFieldText by remember { mutableStateOf("") }

    // Collecting fresh UI state data class

    val uiState by reviewViewModel.state.collectAsState()

    val habit = uiState.habitUiModel // Full Habit Object

    val habitName = habit.name

    val habitCompletions = uiState.habitCompletions

    val completionsTotal = uiState.habitCompletions.size // Total streak count

    val currentYearMonth = uiState.currentYearMonth

    val todayDate = uiState.todayDate

    val habitColor = if (habit.color == Color(0L)) MaterialTheme.colorScheme.primary else habit.color

    val habitPalette = uiState.habitPalette

    val hoursAndMinutes = habit.durationMinutes.toDurationString()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        // ----------------------------------------
        // 1. HERO HEADER AREA
        // ----------------------------------------
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = habitName,
                    onValueChange = { reviewViewModel.updateHabitName(it) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        errorBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                    ),
                    textStyle = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                )
            }
            CheckMarkButton(
                isHabitCompletedToday = { habit.isCompletedToday },
                actualElementSize = 64.dp,
                iconsSize = 32.dp,
                habitColor = habitColor
            ) {
                reviewViewModel.toggleHabitCompletion(dateMillis = todayDate)
            }
        }

        // ----------------------------------------
        // 2. METRICS SHOWCASE ROW
        // ----------------------------------------
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                modifier = Modifier,
                title = "Daily Goal",
                value = hoursAndMinutes,
                icon = painterResource(R.drawable.outline_access_time_24),
                habitColor = habitColor
            )

            MetricCard(
                modifier = Modifier,
                title = "Total Days",
                value = "$completionsTotal",
                icon = painterResource(R.drawable.outline_local_fire_department_24),
                habitColor = habitColor
            )
        }

        // ----------------------------------------
        // 3. MINIMAL CALENDAR SECTION
        // ----------------------------------------
        HabitContainer {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .animateContentSize() // Smoothly animates height changes
            ) {
                // Navigation Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ExpressiveIconButton(
                        onClick = { reviewViewModel.changeMonth(-1) },
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        color = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        contentDescription = "Prev"
                    )

                    // Efficient fade for month name

                    Text(
                        text = "${
                            currentYearMonth.month.name.lowercase()
                                .replaceFirstChar { it.uppercase() }
                        } ${currentYearMonth.year}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    ExpressiveIconButton(
                        onClick = { reviewViewModel.changeMonth(1) },
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        color = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        contentDescription = "Next"
                    )
                }

                // Efficient fade for the grid
                Crossfade(
                    targetState = currentYearMonth,
                    label = "GridFade"
                ) { targetMonth ->
                    SimpleCalendarGrid(
                        currentMonth = targetMonth,
                        completedDates = habitCompletions,
                        habitColor = habitColor,
                        onDayClick = { dateMillis ->
                            reviewViewModel.toggleHabitCompletion(dateMillis = dateMillis)
                        }
                    )
                }
            }
        }

        HabitContainer {
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
                    SectionThumbnail(
                        contentDescription = "Allocated Time",
                        imageVector = painterResource(R.drawable.outline_access_time_24),
                        color = habitColor
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
                    TextField(
                        value = if (habit.durationMinutes == 0) "" else habit.durationMinutes.toString(),
                        onValueChange = { newValue ->
                            val duration = newValue.filter { it.isDigit() }.toIntOrNull() ?: 0
                            reviewViewModel.updateNewHabitDuration(duration)
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
                            .background(habitColor.copy(0.1f))
                    ) {
                        Text(
                            hoursAndMinutes,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }

        HabitContainer {
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
                    SectionThumbnail(
                        contentDescription = "Color Selector",
                        imageVector = painterResource(R.drawable.baseline_color_theme_24),
                        color = habitColor
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
                    habitPalette.forEachIndexed { index, color ->
                        ColorBall(color, habit.color == Color(color)) {
                            reviewViewModel.updateNewHabitColor(index)
                        }
                    }
                }

                Button(
                    onClick = { reviewViewModel.updateNewHabitColor(0) },
                    Modifier.fillMaxWidth()
                ) {
                    Text("Use System Default Color Scheme")
                }
            }
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            shape = RoundedCornerShape(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            onClick = {
                isDialogVisible = true
            }
        ) {
            Text(
                "Delete Habit",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onError
            )
        }
        if (isDialogVisible) {
            BasicAlertDialog(onDismissRequest = { isDialogVisible = false }) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Delete Habit",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Are you sure you want to delete this habit?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )

                        TextField(
                            value = dialogTextFieldText,
                            onValueChange = { dialogTextFieldText = it },
                            placeholder = { Text("Type  \"delete\" to confirm") }
                        )
                        Button(onClick = {
                            reviewViewModel.deleteHabit()
                            isDialogVisible = false
                            onHabitDeleted()

                        }, enabled = dialogTextFieldText.lowercase() == "delete") {
                            Text("Delete Habit")
                        }
                    }
                }
            }
        }
    }
}
