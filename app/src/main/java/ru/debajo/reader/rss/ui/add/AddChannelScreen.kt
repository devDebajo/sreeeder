package ru.debajo.reader.rss.ui.add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.di.diViewModel
import ru.debajo.reader.rss.ui.channels.ChannelCard
import ru.debajo.reader.rss.ui.common.AppCard
import ru.debajo.reader.rss.ui.feed.ScrollToTopButton
import ru.debajo.reader.rss.ui.main.navigation.NavGraph

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
fun AddChannelScreen(parentNavController: NavController) {
    val viewModel: AddChannelScreenViewModel = diViewModel()
    val focusRequester = remember { FocusRequester() }
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(key1 = viewModel, block = {
        viewModel.requestFocus.observe(lifecycleOwner) { focusRequester.requestFocus() }
        viewModel.requestFocus()
    })
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    val keyboardActions = remember(softwareKeyboardController) {
        KeyboardActions(onSearch = {
            viewModel.onLoadClick()
            softwareKeyboardController?.hide()
        })
    }
    Scaffold(
        topBar = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    softwareKeyboardController?.hide()
                    parentNavController.popBackStack()
                }) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = null)
                }
                Text(
                    text = stringResource(R.string.add_channel_title),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .weight(1f)
                )
            }
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            val text by viewModel.text.collectAsState()
            OutlinedTextField(
                value = text,
                onValueChange = { viewModel.onTextChanged(it) },
                singleLine = true,
                keyboardActions = keyboardActions,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                label = { Text(stringResource(R.string.add_channel_placeholder)) },
                shape = RoundedCornerShape(18.dp),
                trailingIcon = {
                    if (text.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onTextChanged("") }) {
                            Icon(Icons.Rounded.Clear, null)
                        }
                    }
                }
            )

            AppCard(
                bgColor = MaterialTheme.colorScheme.primaryContainer,
                onClick = { viewModel.onLoadClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = stringResource(R.string.add_channel_load),
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            val state by viewModel.state.collectAsState()
            when (val stateLocal = state) {
                is AddChannelScreenState.NotFound -> NoDataLoaded(LoadingState.ERROR, stringResource(R.string.add_channel_loading_not_found))
                is AddChannelScreenState.Loading -> NoDataLoaded(LoadingState.LOADING, stringResource(R.string.add_channel_loading))
                is AddChannelScreenState.Idle -> Unit
                is AddChannelScreenState.Loaded -> {
                    val listScrollState = rememberLazyListState()
                    ScrollToTopButton(listScrollState = listScrollState) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            state = listScrollState,
                            content = {
                                items(
                                    count = stateLocal.channels.size,
                                    key = { stateLocal.channels[it].url.url }
                                ) {
                                    val channel = stateLocal.channels[it]
                                    ChannelCard(channel = channel) {
                                        NavGraph.ArticlesList.navigate(parentNavController, channel)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NoDataLoaded(state: LoadingState, message: String) {
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        onClick = { }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when (state) {
                LoadingState.LOADING -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 1.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                LoadingState.ERROR -> Icon(
                    imageVector = Icons.Rounded.Error,
                    tint = MaterialTheme.colorScheme.error,
                    contentDescription = null
                )
            }
            Spacer(Modifier.size(20.dp))
            Text(message)
        }
    }
}

private enum class LoadingState {
    LOADING,
    ERROR
}


