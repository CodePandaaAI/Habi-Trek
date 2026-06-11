package com.liftley.habitrek.core.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.liftley.habitrek.core.ui.navigation.NavRoutes
import com.liftley.habitrek.core.ui.navigation.NavigationViewModel

@Composable
fun HabiTrekNavigationBar(navigationViewModel: NavigationViewModel) {
    // Show bottom bar only on Home and Search screens
    if (navigationViewModel.checkStack(NavRoutes.Home)
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
                selected = navigationViewModel.checkStack(NavRoutes.Home),
                onClick = {
                    if (!navigationViewModel.checkStack(NavRoutes.Home)) {
                        navigationViewModel.removeAllExceptHome()
                    }
                }
            )
        }
    }
}