package com.xvsx.shelf.userInterface.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.xvsx.shelf.userInterface.element.MulticolorProgressBar
import com.xvsx.shelf.userInterface.viewModel.ChatViewModel
import org.koin.compose.koinInject

class ChatScreen() : Screen {
    companion object {
        const val TAG = "ChatScreen"
    }

    @OptIn(ExperimentalComposeUiApi::class, InternalVoyagerApi::class)
    @Composable
    override fun Content() {
        val chatViewModel: ChatViewModel = koinInject()
        val snackbarHostState = remember { SnackbarHostState() }

        BackHandler(enabled = true) {}

        Box(
            modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
                .background(Color.Black),
        ) {
            ContentView(chatViewModel.state)
            MulticolorProgressBar(visibilityStatus = chatViewModel.state.progressBarVisibilityStatus)
            key(chatViewModel.state.uiNotificationMessage) {
                LaunchedEffect(Unit) {
                    chatViewModel.state.uiNotificationMessage?.let {
                        snackbarHostState.showSnackbar(it)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ContentView(
        state: ChatViewModel.State
    ) {
        val listState = rememberLazyListState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Public chat") },
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .imePadding(),
            ) {
                state.chatMessageEntityList?.let { nnChatMessageEntityList ->
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(nnChatMessageEntityList, key = { it.id }) { message ->
                            Text(
                                text = message.text ?: "",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}