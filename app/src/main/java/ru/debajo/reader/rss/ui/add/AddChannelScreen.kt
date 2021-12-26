package ru.debajo.reader.rss.ui.add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
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
import ru.debajo.reader.rss.ui.common.Material3TextField
import ru.debajo.reader.rss.ui.main.navigation.NavGraph

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
fun AddChannelScreen(parentNavController: NavController) {
    val viewModel: AddChannelScreenViewModel = diViewModel()
    Scaffold(
        topBar = {
            Text(
                text = stringResource(R.string.add_channel_title),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )
        }
    ) {
        val softwareKeyboardController = LocalSoftwareKeyboardController.current
        val focusRequester = remember { FocusRequester() }
        val keyboardActions = remember(softwareKeyboardController) {
            KeyboardActions(onSearch = {
                viewModel.onLoadClick()
                softwareKeyboardController?.hide()
            })
        }
        LaunchedEffect(key1 = "AddChannelScreen", block = { focusRequester.requestFocus() })
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            val text by viewModel.text.collectAsState()
            Material3TextField(
                focusRequester = focusRequester,
                value = text,
                singleLine = true,
                keyboardActions = keyboardActions,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                onValueChange = { viewModel.onTextChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.add_channel_placeholder)) }
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
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
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


