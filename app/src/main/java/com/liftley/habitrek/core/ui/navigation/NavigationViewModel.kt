package com.liftley.habitrek.core.ui.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(): ViewModel() {

    val backStack = mutableStateListOf<NavRoutes>(NavRoutes.Home)

    fun addScreen(screen: NavRoutes) {
        backStack.add(screen)
    }

    fun removeAllExceptHome() {
        backStack.removeAll { it != NavRoutes.Home }
    }

    fun removeScreen() {
        if (backStack.lastOrNull() != NavRoutes.Home && backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }

    fun checkStack(route: NavRoutes): Boolean {
        return backStack.lastOrNull() == route
    }
}