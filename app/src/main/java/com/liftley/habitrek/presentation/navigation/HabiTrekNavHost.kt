package com.liftley.habitrek.presentation.navigation

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.liftley.habitrek.core.theme.HabiTrekExpressiveTheme
import com.liftley.habitrek.core.ui.components.HabiTrekFloatingActionButton
import com.liftley.habitrek.core.ui.components.HabiTrekNavigationBar
import com.liftley.habitrek.core.ui.components.HabiTrekTopBarSelector
import com.liftley.habitrek.core.ui.navigation.NavRoutes
import com.liftley.habitrek.core.ui.navigation.NavigationViewModel
import com.liftley.habitrek.presentation.featureAddHabitScreen.AddHabitScreen
import com.liftley.habitrek.presentation.featureHomeScreen.HomScreen
import com.liftley.habitrek.presentation.featureReviewScreen.ReviewScreen

@Composable
fun HabiTrekNavHost() {
    val navigationViewModel = hiltViewModel<NavigationViewModel>(viewModelStoreOwner = LocalActivity.current as ComponentActivity)
    HabiTrekExpressiveTheme {
        Scaffold(
            topBar = { HabiTrekTopBarSelector(navigationViewModel) },
            bottomBar = { HabiTrekNavigationBar(navigationViewModel) },
            floatingActionButton = {
                if (navigationViewModel.checkStack(NavRoutes.Home)) {
                    HabiTrekFloatingActionButton {
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
