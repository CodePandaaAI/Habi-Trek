package com.liftley.habitrek.core.ui.navigation


sealed interface NavRoutes {
    data object Home: NavRoutes
    data object AddHabit: NavRoutes

    data class ReviewHabit(val habitId: Int): NavRoutes

    data object SearchScreen : NavRoutes
}