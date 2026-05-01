package com.liftley.habitrek.presentation.featureWebSearch

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.liftley.habitrek.R
import com.liftley.habitrek.core.ui.components.HabiTrekSurface
import com.liftley.habitrek.presentation.featureWebSearch.components.SearchResultItem
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
        HabiTrekSurface {
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
                        LoadingIndicator()
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
                    LoadingIndicator()
                }
            }

            is SearchScreenState.Success -> {
                if (result.articles.isEmpty()) {
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
                            Text("No Results Found!", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        itemsIndexed(result.articles) { index, item ->
                            val topBottomDp: Pair<Dp, Dp> = when {
                                index == 0 -> if (result.articles.size > 1) Pair(
                                    24.dp,
                                    8.dp
                                ) else Pair(24.dp, 24.dp)

                                index < result.articles.size - 1 -> Pair(8.dp, 8.dp)
                                else -> Pair(8.dp, 24.dp)
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
                Box(
                    Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = result.message,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}