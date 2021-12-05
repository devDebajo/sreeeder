package ru.debajo.reader.rss.ui.add

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
@OptIn(ExperimentalMaterial3Api::class)
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
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            val text by viewModel.text.collectAsState()
            Material3TextField(
                value = text,
                onValueChange = { viewModel.onTextChanged(it) },
                modifier = Modifier
                    .fillMaxWidth(),
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
                is AddChannelScreenState.Error -> NoDataLoaded(LoadingState.ERROR, "Ошибка загрузки канала")
                is AddChannelScreenState.Idle -> Unit
                is AddChannelScreenState.Loaded -> {
                    ChannelCard(channel = stateLocal.channel) {
                        parentNavController.popBackStack()
                        NavGraph.ArticlesList.navigate(parentNavController, stateLocal.channel)
                    }
                }
                is AddChannelScreenState.Loading -> NoDataLoaded(LoadingState.LOADING, "Загрузка канала")
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
                LoadingState.ERROR -> Icon(Icons.Rounded.Error, contentDescription = null)
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


