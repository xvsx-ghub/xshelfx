package com.xvsx.shelf.userInterface.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xvsx.shelf.data.local.RepositoryLocal
import com.xvsx.shelf.data.local.dataBase.entity.ChatMessageEntity
import com.xvsx.shelf.data.local.dataBase.entity.ContactEntity
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
        val chatMessageEntityList: List<ChatMessageEntity>?,
        val currentUserName: String?,
        val currentContactName: String?
    )

    var state by mutableStateOf(
        State(
            uiNotificationMessage = null,
            progressBarVisibilityStatus = false,
            chatMessageEntityList = null,
            currentUserName = null,
            currentContactName = null
        )
    )
        private set

    fun refreshState() {
        state = state.copy(
            currentUserName = repositoryLocal.getCurrentUserName(),
            currentContactName = repositoryLocal.getCurrentContactName()
        )
    }

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
        state = state.copy(
            currentUserName = repositoryLocal.getCurrentUserName(),
            currentContactName = repositoryLocal.getCurrentContactName()
        )
        serveChatMessagesLocal()
        serveChatMessagesRemote()
    }

    private fun serveChatMessagesLocal() {
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

    private fun serveChatMessagesRemote() {
        viewModelScope.launch {
            while (true) {
                repositoryRemote.getChatMessageList(
                    state.currentUserName ?: "anonymous",
                    state.currentContactName ?: "anonymous",
                    ""
                ) { status, data, error ->
                    error?.let {
                        return@getChatMessageList
                    }
                    when (status) {
                        HttpClientCore.HttpStatus.Completed -> {
                            data?.let { chatMessageResponse ->
                                chatMessageResponse.mapToChatMessageEntityList()
                                    ?.let { nnRemoteChatMessageEntity ->
                                        if (nnRemoteChatMessageEntity.isEmpty()){
                                            repositoryLocal.clearChatMessageEntityList()
                                            return@getChatMessageList
                                        }

                                        repositoryLocal.getChatMessageEntityList()
                                            ?.let { nnLocalChatMessageEntity ->
                                                if (nnLocalChatMessageEntity.isEmpty()) {
                                                    repositoryLocal.clearChatMessageEntityList()
                                                    repositoryLocal.insertChatMessageEntityList(
                                                        nnRemoteChatMessageEntity
                                                    )
                                                } else {
                                                    if (nnLocalChatMessageEntity.last().createdAt != nnRemoteChatMessageEntity.last().createdAt) {
                                                        repositoryLocal.clearChatMessageEntityList()
                                                        repositoryLocal.insertChatMessageEntityList(
                                                            nnRemoteChatMessageEntity
                                                        )
                                                    }
                                                }
                                            }
                                    }
                            }
                        }

                        else -> {}
                    }
                }

                delay(1000)
            }
        }
    }

    fun getChatMessages() {
        viewModelScope.launch {
            repositoryRemote.getChatMessageList(
                state.currentUserName ?: "anonymous",
                state.currentContactName ?: "anonymous",
                ""
            ) { status, data, error ->
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
        text: String,
        onSuccess: (chatMessageEntity: ChatMessageEntity) -> Unit
    ) {
        viewModelScope.launch {
            repositoryRemote.setChatMessage(
                state.currentUserName ?: "anonymous",
                state.currentContactName ?: "anonymous",
                text
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
                            onSuccess(chatMessageResponse.mapToChatMessageEntity())
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

    fun updateCurrentUser(name: String) {
        state = state.copy(currentUserName = name)
        repositoryLocal.setCurrentUserName(name)
    }

    fun updateCurrentContact(name: String) {
        state = state.copy(currentContactName = name)
        repositoryLocal.setCurrentContactName(name)
    }

    fun createIfNewContact(
        nickname: String,
        onSuccess: (contactEntity: ContactEntity) -> Unit
    ) {
        if (nickname.isEmpty()) return
        viewModelScope.launch {
            val existingContactList = repositoryLocal.getContactEntityListByNickname(nickname)
            if (!existingContactList.isNullOrEmpty()) return@launch

            var newContactEntity = ContactEntity(nickname = nickname)
            val id = repositoryLocal.insertContactEntity(newContactEntity)
            newContactEntity = newContactEntity.copy(id = id)
            onSuccess(newContactEntity)
            setUiNotification("$nickname added to contact list")
        }
    }
}