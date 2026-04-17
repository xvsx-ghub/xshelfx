package com.xvsx.shelf.userInterface.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.xvsx.shelf.data.local.dataBase.entity.ChatMessageEntity
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

        BackHandler(enabled = true) {}

        Box(
            modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
                .background(Color.Black),
        ) {
            ContentView(chatViewModel)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ContentView(
        chatViewModel: ChatViewModel
    ) {
        val snackbarHostState = remember { SnackbarHostState() }
        val focusManager = LocalFocusManager.current

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    focusManager.clearFocus()
                },
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    var draft by remember { mutableStateOf("") }
                    var isFocused by remember { mutableStateOf(false) }

                    LaunchedEffect(chatViewModel.state.userEntity) {
                        draft = chatViewModel.state.userEntity?.nickname ?: ""
                    }

                    OutlinedTextField(
                        modifier = Modifier
                            .weight(1f)
                            .onFocusChanged { focusState ->
                            if (isFocused && !focusState.isFocused) {
                                chatViewModel.updateUser(draft)
                            }
                            isFocused = focusState.isFocused
                        },
                        value = draft,
                        onValueChange = {
                            draft = it
                        },
                        placeholder = { Text("Nickname") },
                        singleLine = true
                    )
                }
            },
            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .imePadding(),
                ) {
                    val listState = rememberLazyListState()

                    chatViewModel.state.chatMessageEntityList?.let { nnChatMessageEntityList ->
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(nnChatMessageEntityList, key = { it.id }) { message ->
                                if(chatViewModel.state.userEntity?.nickname == message.nickname){
                                    OutcomingChatMessage(message)
                                }else{
                                    IncomingChatMessage(message)
                                }
                            }
                        }

                        LaunchedEffect(nnChatMessageEntityList.size) {
                            if(nnChatMessageEntityList.isNotEmpty()) {
                                listState.animateScrollToItem(nnChatMessageEntityList.size - 1)
                            }
                        }
                    }
                }
            },
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    var draft by remember { mutableStateOf("") }

                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = draft,
                        onValueChange = { draft = it },
                        placeholder = { Text("Message") },
                        singleLine = false,
                        maxLines = 4,
                    )
                    TextButton(
                        onClick = {
                            chatViewModel.setChatMessages(draft)
                        },
                    ) {
                        Text("Send")
                    }
                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        )

        MulticolorProgressBar(visibilityStatus = chatViewModel.state.progressBarVisibilityStatus)
        key(chatViewModel.state.uiNotificationMessage) {
            LaunchedEffect(Unit) {
                chatViewModel.state.uiNotificationMessage?.let {
                    snackbarHostState.showSnackbar(it)
                }
            }
        }
    }

    @Composable
    fun OutcomingChatMessage(chatMessageEntity: ChatMessageEntity) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = chatMessageEntity.text ?: "",
                color = Color.Black,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = chatMessageEntity.createdAt ?: "",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }

    @Composable
    fun IncomingChatMessage(chatMessageEntity: ChatMessageEntity) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = chatMessageEntity.nickname ?: "",
                color = Color.Gray,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = chatMessageEntity.text ?: "",
                color = Color.Black,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = chatMessageEntity.createdAt ?: "",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}