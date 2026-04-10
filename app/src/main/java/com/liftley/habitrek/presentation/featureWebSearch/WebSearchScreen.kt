package com.liftley.habitrek.presentation.featureWebSearch

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.liftley.habitrek.R
import com.liftley.habitrek.core.ui.components.HabitContainer
import com.liftley.habitrek.presentation.featureWebSearch.model.ResultItem
import com.liftley.habitrek.presentation.featureWebSearch.model.SearchScreenState

@Composable
fun SearchScreen() {
    // Get the ViewModel
    val searchViewModel =
        hiltViewModel<SearchViewModel>(viewModelStoreOwner = LocalActivity.current as ComponentActivity)
    val state = searchViewModel.state.collectAsState()
    val query = searchViewModel.query.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        // --- THE SEARCH BAR ---
        HabitContainer {
            Row(
                Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = query.value,
                    onValueChange = { searchViewModel.onQueryChange(it) },
                    modifier = Modifier
                        .weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    textStyle = MaterialTheme.typography.titleLarge,
                    placeholder = {
                        Text("Search", style = MaterialTheme.typography.titleLarge)
                    },
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        searchViewModel.search()
                        keyboardController?.hide()
                    }),
                    // Applying the requested container color and keeping the outline
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.surface,
                        unfocusedBorderColor = MaterialTheme.colorScheme.surface,
                        disabledBorderColor = MaterialTheme.colorScheme.surface,
                    )
                )

                IconButton(
                    onClick = {
                        searchViewModel.search()
                        keyboardController?.hide()
                    },
                    modifier = Modifier.height(48.dp),
                    enabled = state.value != SearchScreenState.Loading,
                    colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    if (state.value == SearchScreenState.Loading) {
                        CircularProgressIndicator()
                    } else {
                        Icon(painterResource(R.drawable.outline_search_24), "Search")
                    }
                }
            }
        }

        when (val result = state.value) {
            is SearchScreenState.Idle -> {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Search for anything! 😇", style = MaterialTheme.typography.titleLarge)
                }

            }

            is SearchScreenState.Loading -> {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is SearchScreenState.Success -> {
                if (result.searchResult.isEmpty()) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                painterResource(R.drawable.outline_emoji_nature_24),
                                "Decorative",
                                Modifier.size(48.dp)
                            )

                            Text(
                                "No Results Found!",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }

                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        itemsIndexed(result.searchResult) { index, item ->
                            val topBottomDp: Pair<Dp, Dp> = when {
                                index == 0 -> {
                                    if (result.searchResult.size > 1) {
                                        Pair(24.dp, 8.dp)
                                    } else {
                                        Pair(24.dp, 24.dp)
                                    }
                                }

                                index < result.searchResult.size - 1 -> {
                                    Pair(8.dp, 8.dp)
                                }

                                else -> {
                                    Pair(8.dp, 24.dp)
                                }
                            }

                            SearchResultItem(
                                item,
                                top = topBottomDp.first,
                                bottom = topBottomDp.second
                            )
                        }
                    }
                }
            }

            is SearchScreenState.Error -> {
                result.error?.let {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

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

@Composable
fun SearchResultContainer(
    modifier: Modifier = Modifier,
    isPressed: () -> Boolean,
    top: Dp = 8.dp,
    bottom: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    // sizeAnimation uses simple FloatAnimation to animate towards a target value 0.9f is "isPressed" is true and vice versa to 1f if not
    val scaleAnimation = animateFloatAsState(targetValue = if (isPressed()) 0.9f else 1f, spring())

    Box(
        modifier
            .graphicsLayer {
                scaleX = scaleAnimation.value
                scaleY = scaleAnimation.value
            }
            .clip(
                RoundedCornerShape(
                    topStart = top,
                    topEnd = top,
                    bottomStart = bottom,
                    bottomEnd = bottom
                )
            )
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
