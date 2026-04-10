package com.liftley.habitrek.presentation.featureHomeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.liftley.habitrek.R
import com.liftley.habitrek.presentation.featureHomeScreen.components.HabitCard
import com.liftley.habitrek.core.designSystem.theme.LiftleyTheme

@Composable
fun HomScreen(onHabitClick: (Int) -> Unit) {
    val homeViewModel = hiltViewModel<HomeViewModel>()
    val uiState by homeViewModel.state.collectAsState()

    if (uiState.habits.isEmpty()) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painterResource(R.drawable.outline_emoji_nature_24),
                    "Decorative",
                    Modifier.size(48.dp)
                )

                Text("No Habits Added Yet!", style = MaterialTheme.typography.titleLarge)
            }

        }
    }
    else {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(uiState.habits) { index, habit ->
                val topBottomDp: Pair<Dp, Dp> = when {
                    index == 0 -> {
                        if (uiState.habits.size > 1) {
                            Pair(24.dp, 8.dp)
                        } else {
                            Pair(24.dp, 24.dp)
                        }
                    }

                    index < uiState.habits.size - 1 -> {
                        Pair(8.dp, 8.dp)
                    }

                    else -> {
                        Pair(8.dp, 24.dp)
                    }
                }
                HabitCard(
                    habitName = habit.name,
                    habitDurationMinutes = habit.durationMinutes,
                    habitColor = habit.color,
                    isCompletedToday = habit.isCompletedToday,
                    topRounding = topBottomDp.first,
                    bottomRounding = topBottomDp.second,
                    onClickListener = { onHabitClick(habit.id) },
                    onCompletedClick = { homeViewModel.toggleHabitCompletion(habit.id) }
                )
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
