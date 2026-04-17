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
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.xvsx.shelf.data.local.dataBase.entity.ContactEntity
import com.xvsx.shelf.userInterface.element.MulticolorProgressBar
import com.xvsx.shelf.userInterface.viewModel.HomeViewModel
import org.koin.compose.koinInject

class HomeScreen() : Screen {
    companion object {
        const val TAG = "ChatScreen"
    }

    @OptIn(ExperimentalComposeUiApi::class, InternalVoyagerApi::class)
    @Composable
    override fun Content() {
        val homeViewModel: HomeViewModel = koinInject()

        BackHandler(enabled = true) {}

        Box(
            modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
                .background(Color.Black),
        ) {
            ContentView(homeViewModel)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ContentView(
        homeViewModel: HomeViewModel
    ) {
        val snackbarHostState = remember { SnackbarHostState() }
        val focusManager = LocalFocusManager.current
        val navigator = LocalNavigator.current
        val chatScreen: ChatScreen = koinInject()

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
                    TextButton(
                        onClick = {
                            navigator?.push(chatScreen)
                        },
                    ) {
                        Text("Chat")
                    }

                    var draft by remember { mutableStateOf("") }
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = draft,
                        onValueChange = {
                            draft = it
                            homeViewModel.searchContact(draft) {}
                        },
                        placeholder = {
                            Text(
                                "Nickname",
                                color = Color.Gray
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                }
            },
            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                ) {
                    val listState = rememberLazyListState()

                    Text(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        text = "Contacts"
                    )

                    homeViewModel.state.contactEntityList?.let { nnContactEntityList ->
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(nnContactEntityList, key = { it.id }) { item ->
                                Contact(item){contactEntity ->
                                    homeViewModel.deleteContact(contactEntity)
                                }
                            }
                        }

                        LaunchedEffect(nnContactEntityList.size) {
                            if (nnContactEntityList.isNotEmpty()) {
                                listState.animateScrollToItem(nnContactEntityList.size - 1)
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
                        placeholder = {
                            Text(
                                "Nickname",
                                color = Color.Gray
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                    TextButton(
                        onClick = {
                            homeViewModel.createContact(draft) {
                                draft = ""
                            }
                        },
                    ) {
                        Text("Add")
                    }
                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        )

        MulticolorProgressBar(visibilityStatus = homeViewModel.state.progressBarVisibilityStatus)
        key(homeViewModel.state.uiNotificationMessage) {
            LaunchedEffect(Unit) {
                homeViewModel.state.uiNotificationMessage?.let {
                    snackbarHostState.showSnackbar(it)
                }
            }
        }
    }

    @Composable
    fun Contact(contactEntity: ContactEntity, onDelete: (contactEntity: ContactEntity) -> Unit) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = contactEntity.nickname ?: "",
                color = Color.Black,
                fontSize = 16.sp
            )
            TextButton(
                onClick = {
                    onDelete(contactEntity)
                },
            ) {
                Text("Delete")
            }
        }
    }
}