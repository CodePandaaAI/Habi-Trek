package com.liftley.habitrek.presentation.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.liftley.habitrek.core.ui.components.AddHabitFloatingActionButton
import com.liftley.habitrek.core.ui.components.AppTopBar
import com.liftley.habitrek.core.ui.components.AppTopBarWithBackButton
import com.liftley.habitrek.core.ui.navigation.NavRoutes
import com.liftley.habitrek.core.ui.navigation.NavigationViewModel
import com.liftley.habitrek.presentation.featureAddHabitScreen.AddHabitScreen
import com.liftley.habitrek.presentation.featureHomeScreen.HomScreen
import com.liftley.habitrek.presentation.featureReviewScreen.ReviewScreen
import com.liftley.habitrek.presentation.featureWebSearch.SearchScreen
import com.liftley.habitrek.core.designSystem.theme.LiftleyTheme

@Composable
fun HabiTrekNavHost() {
    val navigationViewModel = hiltViewModel<NavigationViewModel>()
    LiftleyTheme {
        Scaffold(
            topBar = {
                if (navigationViewModel.checkStack(NavRoutes.Home)) {
                    AppTopBar { "Habi Trek" }
                } else if (navigationViewModel.checkStack(NavRoutes.AddHabit)) {
                    AppTopBarWithBackButton(onBack = { navigationViewModel.removeScreen() }) { "Add Habit" }
                } else {
                    AppTopBarWithBackButton(onBack = { navigationViewModel.removeScreen() }) { "Review Habit" }
                }
            },
            bottomBar = {
                // Show bottom bar only on Home and Search screens
                if (navigationViewModel.checkStack(NavRoutes.Home) || navigationViewModel.checkStack(
                        NavRoutes.SearchScreen
                    )
                ) {
                    NavigationBar(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .padding(horizontal = 32.dp, vertical = 16.dp)
                            .clip(RoundedCornerShape(32.dp)), // Rounds the corners
                        containerColor = MaterialTheme.colorScheme.surface,
                        windowInsets = WindowInsets(0, 0, 0, 0),
                        tonalElevation = 0.dp
                    ) {

                        NavigationBarItem(
                            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                            label = { Text("Home") },
                            selected = navigationViewModel.checkStack(NavRoutes.Home), // Replace with your selection logic
                            onClick = {
                                if(!navigationViewModel.checkStack(NavRoutes.Home)){
                                    navigationViewModel.removeAllExceptHome()
                                }
                            }
                        )

                        NavigationBarItem(
                            icon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                            label = { Text("Search") },
                            selected = navigationViewModel.checkStack(NavRoutes.SearchScreen), // Replace with your selection logic
                            onClick = {
                                if(!navigationViewModel.checkStack(NavRoutes.SearchScreen)){
                                    navigationViewModel.addScreen(NavRoutes.SearchScreen)
                                }
                            }
                        )
                    }
                }
            },
            floatingActionButton = {
                if (navigationViewModel.checkStack(NavRoutes.Home)) {
                    AddHabitFloatingActionButton {
                        navigationViewModel.addScreen(NavRoutes.AddHabit)
                    }
                }
            },
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ) { innerPadding ->
            NavDisplay(
                backStack = navigationViewModel.backStack,
                onBack = { navigationViewModel.removeScreen() },
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                modifier = Modifier.padding(innerPadding)
            ) { key ->
                when (key) {
                    NavRoutes.Home -> {
                        NavEntry(key) {
                            HomScreen {
                                navigationViewModel.addScreen(NavRoutes.ReviewHabit(it))
                            }
                        }
                    }

                    NavRoutes.AddHabit -> {
                        NavEntry(key) {
                            AddHabitScreen(onAddHabitClick = { navigationViewModel.removeScreen() })
                        }
                    }

                    NavRoutes.SearchScreen -> {
                        NavEntry(key) {
                            SearchScreen()
                        }
                    }

                    is NavRoutes.ReviewHabit -> {
                        NavEntry(key) {
                            ReviewScreen(habitId = key.habitId) {
                                navigationViewModel.removeScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AppRootPreview() {

}