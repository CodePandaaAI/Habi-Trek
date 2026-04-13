package com.liftley.habitrek.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.liftley.habitrek.R

@Composable
fun HabiTrekSectionThubnail(
    imageVector: Painter,
    contentDescription: String?,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val isLight = color.luminance() > 0.6f
    val iconColor =
        if (isLight && !isSystemInDarkTheme()) MaterialTheme.colorScheme.onSurface
        else MaterialTheme.colorScheme.surface

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .size(height = 56.dp, width = 48.dp)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector,
            contentDescription,
            tint = iconColor,
            modifier = Modifier
                .size(24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HabiTrekSectionThubnailPreview() {
    HabiTrekSectionThubnail(
        contentDescription = "Section Thumbnail",
        imageVector = painterResource(R.drawable.outline_heart_smile_24)
    )
}