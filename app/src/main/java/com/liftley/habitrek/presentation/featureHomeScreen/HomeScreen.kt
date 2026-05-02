package com.liftley.habitrek.presentation.featureHomeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.liftley.habitrek.core.theme.HabiTrekExpressiveTheme
import com.liftley.habitrek.core.ui.components.HabiTrekEmptyScreen
import com.liftley.habitrek.core.ui.components.HabiTrekLoadingScreen
import com.liftley.habitrek.presentation.featureHomeScreen.components.AiSummaryCard
import com.liftley.habitrek.presentation.featureHomeScreen.components.HabitCard
import com.liftley.habitrek.presentation.featureHomeScreen.model.HomeUiState

@Composable
fun HomScreen(onHabitClick: (Int) -> Unit) {
    val homeViewModel = hiltViewModel<HomeViewModel>()
    var isSummaryExpanded by remember { mutableStateOf(true) }
    when (val uiState = homeViewModel.state.collectAsState().value) {
        HomeUiState.Loading -> HabiTrekLoadingScreen()
        HomeUiState.Empty -> HabiTrekEmptyScreen()
        is HomeUiState.Error -> {
            Box(
                Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Search,
                        "Decorative",
                        Modifier.size(48.dp)
                    )

                    Text(uiState.message, style = MaterialTheme.typography.titleLarge)
                }
            }
        }

        is HomeUiState.Success -> {
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 96.dp
                ),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // AI Top Card Section
                item {
                    AiSummaryCard(
                        state = { uiState },
                        onGenerateSummaryClick = {
                            homeViewModel.onGenerateSummaryClick()
                            isSummaryExpanded = true
                        },
                        isSummaryExpanded = { isSummaryExpanded }
                    ) {
                        isSummaryExpanded = it
                    }
                }

                // Existing Habit Cards
                itemsIndexed(
                    items = uiState.habits,
                    key = { _, habit -> habit.id }
                ) { index, habit ->
                    val topRounding = if (index == 0) 24.dp else 4.dp
                    val bottomRounding = if (index == uiState.habits.lastIndex) 24.dp else 4.dp
                    val habitColor =
                        if (habit.color == Color(0UL)) MaterialTheme.colorScheme.primary else habit.color

                    HabitCard(
                        habitName = habit.name,
                        habitDurationMinutes = habit.durationMinutes,
                        habitColor = habitColor,
                        isCompletedToday = habit.isCompletedToday,
                        topRounding = topRounding,
                        bottomRounding = bottomRounding,
                        onClickListener = { onHabitClick(habit.id) },
                        onCompletedClick = { homeViewModel.toggleHabitCompletion(habit.id) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomScreenPreview() {
    HabiTrekExpressiveTheme {
        HomScreen {}
    }
}
