package com.liftley.habitrek.presentation.featureHomeScreen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.liftley.habitrek.core.ui.components.ExpressiveIconButton
import com.liftley.habitrek.presentation.featureHomeScreen.model.HomeUiState

@Composable
fun AiSummaryCard(
    state: () -> HomeUiState.Success,
    onGenerateSummaryClick: () -> Unit,
    isSummaryExpanded: () -> Boolean,
    onToggleClick: (v: Boolean) -> Unit

) {
    val uiState = state()
    val isSummaryExpanded = isSummaryExpanded()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
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
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!uiState.isGeneratingSummary) {
                        ExpressiveIconButton(
                            color = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            ),
                            onClick = { onGenerateSummaryClick() },
                            modifier = Modifier,
                            imageVector = Icons.Default.Refresh,
                            contentDescription = if (uiState.aiSummary != null) "Regenerate Summary"
                            else "Generate Summary"
                        )
                    }
                    if (!uiState.isGeneratingSummary && uiState.aiSummary != null) {
                        ExpressiveIconButton(
                            color = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            ),
                            onClick = { onToggleClick(!isSummaryExpanded) },
                            modifier = Modifier,
                            imageVector = if (isSummaryExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }
                }
            }
            if (uiState.aiSummary == null && !uiState.isGeneratingSummary) {
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Click Refresh to Generate Summary",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            if (uiState.aiSummary == null && !uiState.isGeneratingSummary) return@Column
            Surface(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (uiState.isGeneratingSummary) {
                        LoadingIndicator(modifier = Modifier.padding(16.dp))
                    } else {
                        AnimatedVisibility(visible = isSummaryExpanded) {
                            uiState.aiSummary?.let { text ->
                                Text(
                                    text = text,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .align(Alignment.Start),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}