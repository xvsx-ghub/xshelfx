package com.xvsx.shelf.userInterface.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xvsx.shelf.data.local.RepositoryLocal
import com.xvsx.shelf.data.local.dataBase.entity.ChatMessageEntity
import com.xvsx.shelf.data.remote.RepositoryRemote
import com.xvsx.shelf.data.remote.http.HttpClientCore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repositoryRemote: RepositoryRemote,
    private val repositoryLocal: RepositoryLocal
) : ViewModel() {
    companion object Companion {
        const val TAG = "ChatViewModel"
    }

    data class State(
        val uiNotificationMessage: String?,
        val progressBarVisibilityStatus: Boolean,
        val chatMessageEntityList: List<ChatMessageEntity>?
    )

    var state by mutableStateOf(
        State(
            uiNotificationMessage = null,
            progressBarVisibilityStatus = false,
            chatMessageEntityList = null
        )
    )
        private set

    private fun setUiNotification(message: String?) {
        state = state.copy(uiNotificationMessage = message)
        viewModelScope.launch {
            delay(3000)
            state = state.copy(uiNotificationMessage = null)
        }
    }

    private fun pushProgressBar(visible: Boolean) {
        state = state.copy(progressBarVisibilityStatus = visible)
    }

    init {
        serveChatMessagesLocal()
        serveChatMessagesRemote()
    }

    fun serveChatMessagesLocal() {
        viewModelScope.launch {
            repositoryLocal.getChatMessageEntityList()?.let {
                state = state.copy(chatMessageEntityList = it)
            }
        }

        viewModelScope.launch {
            repositoryLocal.getChatMessageEntityListAsFlow().collect {
                state = state.copy(chatMessageEntityList = it)
            }
        }
    }

    fun serveChatMessagesRemote() {
        viewModelScope.launch {
            while (true) {
                repositoryRemote.getChatMessageList { status, data, error ->
                    error?.let {
                        return@getChatMessageList
                    }
                    when (status) {
                        HttpClientCore.HttpStatus.Completed -> {
                            data?.let { chatMessageResponse ->
                                chatMessageResponse.mapToChatMessageEntityList()?.let {
                                    repositoryLocal.clearChatMessageEntityList()
                                    repositoryLocal.insertChatMessageEntityList(
                                        it
                                    )
                                }
                            }
                        }

                        else -> {}
                    }
                }

                delay(10000)
            }
        }
    }

    fun getChatMessages() {
        viewModelScope.launch {
            repositoryRemote.getChatMessageList { status, data, error ->
                error?.let {
                    setUiNotification(error.message)
                    pushProgressBar(false)
                    return@getChatMessageList
                }
                when (status) {
                    HttpClientCore.HttpStatus.Started -> {
                        pushProgressBar(true)
                    }

                    HttpClientCore.HttpStatus.Completed -> {
                        data?.let { chatMessageResponse ->
                            chatMessageResponse.mapToChatMessageEntityList()?.let {
                                repositoryLocal.clearChatMessageEntityList()
                                repositoryLocal.insertChatMessageEntityList(
                                    it
                                )
                            }
                        }
                        pushProgressBar(false)
                    }

                    HttpClientCore.HttpStatus.Busy -> {
                        setUiNotification("Can't obtain messages. Ty again later")
                        pushProgressBar(false)
                    }
                }
            }
        }
    }

    fun setChatMessages(
        nickname: String,
        text: String
    ) {
        viewModelScope.launch {
            repositoryRemote.setChatMessage(
                nickname, text
            ) { status, data, error ->
                error?.let {
                    setUiNotification(error.message)
                    pushProgressBar(false)
                    return@setChatMessage
                }
                when (status) {
                    HttpClientCore.HttpStatus.Started -> {
                        pushProgressBar(true)
                    }

                    HttpClientCore.HttpStatus.Completed -> {
                        data?.let { chatMessageResponse ->
                            chatMessageResponse.text
                        }
                        pushProgressBar(false)
                    }

                    HttpClientCore.HttpStatus.Busy -> {
                        setUiNotification("Can't obtain messages. Ty again later")
                        pushProgressBar(false)
                    }
                }
            }
        }
    }
}