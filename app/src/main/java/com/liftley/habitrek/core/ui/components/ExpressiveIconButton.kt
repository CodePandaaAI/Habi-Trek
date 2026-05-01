package com.liftley.habitrek.core.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

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