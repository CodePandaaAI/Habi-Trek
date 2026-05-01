package com.liftley.habitrek.presentation.featureHomeScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.liftley.habitrek.core.ui.components.ExpressiveIconButton
import com.liftley.habitrek.core.ui.components.HabiTrekEmptyScreen
import com.liftley.habitrek.core.ui.components.HabiTrekLoadingScreen
import com.liftley.habitrek.presentation.featureHomeScreen.components.HabitCard
import com.liftley.habitrek.presentation.featureHomeScreen.model.HomeUiState

@Composable
fun HomScreen(onHabitClick: (Int) -> Unit) {
    val homeViewModel = hiltViewModel<HomeViewModel>()
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
                    var isSummaryExpanded by remember { mutableStateOf(false) }
                    val animatedProgress by animateFloatAsState(
                        targetValue = uiState.downloadProgress / 100f,
                        label = "downloadProgress"
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "AI Summary",
                                    modifier = Modifier.padding(start = 16.dp),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                if (!uiState.isDownloading && !uiState.isAiLoading && uiState.aiSummary != null) {
                                    ExpressiveIconButton(
                                        color = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                        onClick = { isSummaryExpanded = !isSummaryExpanded },
                                        modifier = Modifier,
                                        imageVector = if (isSummaryExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = null
                                    )
                                }
                            }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {

                                    if (uiState.isDownloading) {
                                        LinearWavyProgressIndicator(
                                            progress = {
                                                animatedProgress
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                                        )
                                        Surface(
                                            modifier = Modifier.padding(bottom = 8.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.surfaceContainer
                                        ) {
                                            Text(
                                                "Downloading... ${uiState.downloadProgress}%",
                                                style = MaterialTheme.typography.bodySmall,
                                                modifier = Modifier.padding(8.dp),
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        }
                                    } else if (!uiState.isModelDownloaded) {
                                        Button(
                                            onClick = { homeViewModel.downloadAiModel() },
                                            modifier = Modifier.padding(16.dp)
                                        ) {
                                            Text(
                                                "Download Brain (~549MB)",
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )
                                        }
                                        uiState.downloadError?.let {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = MaterialTheme.colorScheme.surfaceContainer,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            ) {
                                                Text(
                                                    text = it,
                                                    color = MaterialTheme.colorScheme.error,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    modifier = Modifier.padding(8.dp)
                                                )
                                            }
                                        }
                                    } else if (uiState.isAiLoading) {
                                        LoadingIndicator(
                                            modifier = Modifier.padding(
                                                horizontal = 16.dp,
                                                vertical = 16.dp
                                            )
                                        )
                                    } else {
                                        Column(
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            verticalArrangement = Arrangement.spacedBy(16.dp),
                                            horizontalAlignment = Alignment.End
                                        ) {
                                            if (uiState.aiSummary != null) {
                                                AnimatedContent(isSummaryExpanded) {
                                                    Text(
                                                        text = if (!it) uiState.aiSummary.take(
                                                            60
                                                        ) + "..." else uiState.aiSummary,
                                                        modifier = Modifier.fillMaxWidth().align(Alignment.Start),
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                                    )
                                                }
                                            }
                                            Button(
                                                onClick = { homeViewModel.onGenerateSummaryClick() },
                                                modifier = Modifier
                                            ) {
                                                Text(
                                                    if (uiState.aiSummary != null) "Regenerate Summary"
                                                    else "Generate Summary"
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
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
