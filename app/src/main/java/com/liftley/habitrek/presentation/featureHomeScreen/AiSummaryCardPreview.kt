package com.liftley.habitrek.presentation.featureHomeScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.liftley.habitrek.core.theme.HabiTrekExpressiveTheme

/**
 * Standalone composable for the AI Summary card, accepting raw state values
 * so it can be previewed without a ViewModel.
 */
@Composable
fun AiSummaryCard(
    isDownloading: Boolean = false,
    downloadProgress: Int = 0,
    isModelDownloaded: Boolean = false,
    isAiLoading: Boolean = false,
    aiSummary: String? = null,
    downloadError: String? = null,
    onDownloadClick: () -> Unit = {},
    onGenerateSummaryClick: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "AI Summary",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                if (isDownloading) {
                    LinearWavyProgressIndicator(
                        progress = { downloadProgress / 100f },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        "Downloading... ${downloadProgress}%",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                } else if (!isModelDownloaded) {
                    Button(
                        onClick = onDownloadClick,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Download Brain (~500MB)")
                    }
                    downloadError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                } else if (isAiLoading) {
                    LinearWavyProgressIndicator()
                    Text(
                        "Generating summary...",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                } else {
                    // Show summary if available
                    if (aiSummary != null) {
                        Text(
                            text = aiSummary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    // Always show "Generate Summary" button when model is ready
                    OutlinedButton(
                        onClick = onGenerateSummaryClick,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(if (aiSummary != null) "✨ Regenerate Summary" else "✨ Generate Summary")
                    }
                }
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────

@Preview(name = "Not Downloaded", showBackground = true)
@Composable
private fun PreviewNotDownloaded() {
    HabiTrekExpressiveTheme {
        AiSummaryCard(
            isModelDownloaded = false
        )
    }
}

@Preview(name = "Not Downloaded + Error", showBackground = true)
@Composable
private fun PreviewNotDownloadedError() {
    HabiTrekExpressiveTheme {
        AiSummaryCard(
            isModelDownloaded = false,
            downloadError = "Network error: connection timed out"
        )
    }
}

@Preview(name = "Downloading 42%", showBackground = true)
@Composable
private fun PreviewDownloading() {
    HabiTrekExpressiveTheme {
        AiSummaryCard(
            isDownloading = true,
            downloadProgress = 42
        )
    }
}

@Preview(name = "AI Loading", showBackground = true)
@Composable
private fun PreviewAiLoading() {
    HabiTrekExpressiveTheme {
        AiSummaryCard(
            isModelDownloaded = true,
            isAiLoading = true
        )
    }
}

@Preview(name = "No Summary Yet", showBackground = true)
@Composable
private fun PreviewNoSummaryYet() {
    HabiTrekExpressiveTheme {
        AiSummaryCard(
            isModelDownloaded = true,
            aiSummary = null
        )
    }
}

@Preview(name = "Summary Ready", showBackground = true)
@Composable
private fun PreviewSummaryReady() {
    HabiTrekExpressiveTheme {
        AiSummaryCard(
            isModelDownloaded = true,
            aiSummary = "Your meditation streak of 45 days is incredible — that kind of consistency rewires your brain. Don't forget your evening walk today, even 10 minutes will reset your energy."
        )
    }
}
