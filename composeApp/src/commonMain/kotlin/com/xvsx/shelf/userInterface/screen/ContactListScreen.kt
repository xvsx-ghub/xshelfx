package com.xvsx.shelf.userInterface.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.wiswm.nav.support.resources.Colors
import com.xvsx.shelf.data.local.dataBase.entity.ContactEntity
import com.xvsx.shelf.userInterface.element.MulticolorProgressBar
import com.xvsx.shelf.userInterface.viewModel.ContactListViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import shelf.composeapp.generated.resources.Res
import shelf.composeapp.generated.resources.ic_add
import shelf.composeapp.generated.resources.ic_chat
import shelf.composeapp.generated.resources.ic_check
import shelf.composeapp.generated.resources.ic_person_add
import shelf.composeapp.generated.resources.ic_person_check
import shelf.composeapp.generated.resources.ic_person_remove
import shelf.composeapp.generated.resources.ic_person_search

class ContactListScreen() : Screen {
    companion object Companion {
        const val TAG = "ChatScreen"
    }

    @OptIn(ExperimentalComposeUiApi::class, InternalVoyagerApi::class)
    @Composable
    override fun Content() {
        val contactListViewModel: ContactListViewModel = koinInject()

        LaunchedEffect(Unit) {
            contactListViewModel.refreshState()
        }

        BackHandler(enabled = true) {}

        Box(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Colors.DarkRoyalBlue,
                            Colors.DeepSpaceBlue
                        )
                    )
                )
        ) {
            ContentView(contactListViewModel)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ContentView(
        contactListViewModel: ContactListViewModel
    ) {
        val snackbarHostState = remember { SnackbarHostState() }
        val focusManager = LocalFocusManager.current
        val navigator = LocalNavigator.current
        val chatScreen: ChatScreen = koinInject()

        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    focusManager.clearFocus()
                },
            topBar = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        var draft by remember { mutableStateOf("") }
                        LaunchedEffect(contactListViewModel.state.currentUserName) {
                            draft = contactListViewModel.state.currentUserName ?: ""
                        }
                        IconButton(
                            onClick = {
                                navigator?.push(chatScreen)
                            }
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_chat),
                                contentDescription = "Chat",
                                tint = Colors.White
                            )
                        }
                        OutlinedTextField(
                            modifier = Modifier
                                .weight(1f),
                            value = draft,
                            onValueChange = {
                                draft = it
                            },
                            placeholder = {
                                Text(
                                    "Set your nickname",
                                    color = Color.Gray
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_person_check),
                                    contentDescription = "User",
                                    tint = Colors.Gray
                                )
                            }
                        )
                        IconButton(
                            onClick = {
                                contactListViewModel.updateCurrentUser(draft)
                            }
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_check),
                                contentDescription = "Approve nickname",
                                tint = Colors.White
                            )
                        }
                    }
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
                            onValueChange = {
                                draft = it
                                contactListViewModel.searchContact(draft) {}
                            },
                            placeholder = {
                                Text(
                                    text = "Search contact",
                                    color = Color.Gray
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Colors.White,
                                unfocusedTextColor = Colors.White
                            ),
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_person_search),
                                    contentDescription = "Search contact",
                                    tint = Colors.Gray
                                )
                            }
                        )
                    }
                }
            },
            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                ) {
                    val listState = rememberLazyListState()
                    contactListViewModel.state.contactEntityList?.let { nnContactEntityList ->
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(nnContactEntityList, key = { it.id }) { item ->
                                Contact(
                                    contactEntity = item,
                                    onDelete = { contactEntity ->
                                        contactListViewModel.deleteContact(contactEntity)
                                    },
                                    onClick = { contactEntity ->
                                        contactEntity.nickname?.let {
                                            contactListViewModel.updateCurrentContact(it)
                                            navigator?.push(chatScreen)
                                        }
                                    }
                                )
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
                                "Add contact",
                                color = Colors.Gray
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Colors.White,
                            unfocusedTextColor = Colors.White
                        ),
                        trailingIcon = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_person_add),
                                contentDescription = "User",
                                tint = Colors.Gray
                            )
                        }
                    )

                    IconButton(
                        onClick = {
                            contactListViewModel.createContact(draft) {
                                draft = ""
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_add),
                            contentDescription = "Add",
                            tint = Colors.White
                        )
                    }
                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        )

        MulticolorProgressBar(visibilityStatus = contactListViewModel.state.progressBarVisibilityStatus)
        key(contactListViewModel.state.uiNotificationMessage) {
            LaunchedEffect(Unit) {
                contactListViewModel.state.uiNotificationMessage?.let {
                    snackbarHostState.showSnackbar(it)
                }
            }
        }
    }

    @Composable
    fun Contact(
        contactEntity: ContactEntity,
        onDelete: (contactEntity: ContactEntity) -> Unit,
        onClick: (contactEntity: ContactEntity) -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick(contactEntity)
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = contactEntity.nickname ?: "",
                color = Colors.White,
                fontSize = 16.sp
            )

            IconButton(
                onClick = {
                    onDelete(contactEntity)
                }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_person_remove),
                    contentDescription = "Delete",
                    tint = Colors.White
                )
            }
        }
    }
}