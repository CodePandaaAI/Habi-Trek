package com.liftley.habitrek.presentation.featureWebSearch.components

import android.content.Intent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.liftley.habitrek.presentation.featureWebSearch.model.ResultItem

@Composable
fun SearchResultItem(item: ResultItem, top: Dp, bottom: Dp) {
    // Mutable State "var isPressed" for checking if ResultItem is clicked or not for running shrink/expand animation
    var isPressed by remember { mutableStateOf(false) }

    val context = LocalContext.current

    SearchResultContainer(isPressed = { isPressed }, top = top, bottom = bottom) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            // 1. FINGER DOWN: Trigger the shrink animation immediately
                            isPressed = true

                            // 2. WAIT: Pause execution and see how the tap resolves
                            tryAwaitRelease()

                            // 3. FINGER UP OR CANCELED: Trigger the release animation
                            // Notice this happens whether the tap succeeded or was stolen! Let it bounce back!
                            isPressed = false
                        },
                        onTap = {
                            // Opens the link in your phone's browser!
                            if (item.link.isNotBlank() && item.link != "No Link") {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    item.link.toUri()
                                )
                                context.startActivity(intent)
                            }
                        }
                    )
                }
        ) {
            Text(
                text = item.title,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.snippet,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}