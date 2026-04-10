package com.liftley.habitrek.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(title: () -> String) {
    TopAppBar(
        title = {
            TopBarTitleBox { title() }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBarWithBackButton(onBack: () -> Unit = {}, title: () -> String) {
    TopAppBar(
        title = {
            TopBarTitleBox { title() }
        },
        navigationIcon = {
            ExpressiveIconButton(
                onClick = onBack,
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back Button"
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    )
}

@Composable
fun TopBarTitleBox(title: () -> String) {
    Box(
        Modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .height(48.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                title(),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun ExpressiveIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    imageVector: ImageVector,
    color: IconButtonColors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surface),
    contentDescription: String?
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = color
    ) {
        Icon(imageVector, contentDescription)
    }
}