package com.liftley.habitrek.presentation.featureHomeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.liftley.habitrek.core.designSystem.theme.LiftleyTheme
import com.liftley.habitrek.core.ui.components.HabiTrekEmptyScreen
import com.liftley.habitrek.core.ui.components.HabiTrekLoadingScreen
import com.liftley.habitrek.core.ui.components.HabiTrekErrorScreen
import com.liftley.habitrek.presentation.featureHomeScreen.components.HabitCard
import com.liftley.habitrek.presentation.featureHomeScreen.model.HomeUiState

@Composable
fun HomScreen(onHabitClick: (Int) -> Unit) {
    val homeViewModel = hiltViewModel<HomeViewModel>()

    when (val uiState = homeViewModel.state.collectAsState().value) {
        HomeUiState.Loading -> {
            HabiTrekLoadingScreen()
        }

        HomeUiState.Empty -> {
            HabiTrekEmptyScreen()
        }

        is HomeUiState.Error -> {
            HabiTrekErrorScreen(message = uiState.message)
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
                itemsIndexed(
                    items = uiState.habits,
                    key = { _, habit -> habit.id }
                ) { index, habit ->
                    val topRounding = if (index == 0) 24.dp else 8.dp
                    val bottomRounding =
                        if (index == uiState.habits.lastIndex) 24.dp else 8.dp

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
    LiftleyTheme {
        HomScreen {}
    }
}
