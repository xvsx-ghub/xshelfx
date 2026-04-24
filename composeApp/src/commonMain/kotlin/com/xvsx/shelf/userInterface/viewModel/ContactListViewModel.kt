package com.xvsx.shelf.userInterface.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xvsx.shelf.data.local.RepositoryLocal
import com.xvsx.shelf.data.local.dataBase.entity.ContactEntity
import com.xvsx.shelf.data.remote.RepositoryRemote
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ContactListViewModel(
    private val repositoryRemote: RepositoryRemote,
    private val repositoryLocal: RepositoryLocal
) : ViewModel() {
    companion object Companion {
        const val TAG = "ChatViewModel"
    }

    data class State(
        val uiNotificationMessage: String?,
        val progressBarVisibilityStatus: Boolean,
        val contactEntityList: List<ContactEntity>?,
        val currentUserName: String?
    )

    var state by mutableStateOf(
        State(
            uiNotificationMessage = null,
            progressBarVisibilityStatus = false,
            contactEntityList = null,
            currentUserName = null
        )
    )
        private set

    fun refreshState(){
        state = state.copy(
            currentUserName = repositoryLocal.getCurrentUserName(),
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
        state = state.copy(currentUserName = repositoryLocal.getCurrentUserName())
        serveContact()
    }

    private fun serveContact() {
        viewModelScope.launch {
            repositoryLocal.getContactEntityList()?.let {
                state = state.copy(contactEntityList = it)
            }
        }

        viewModelScope.launch {
            repositoryLocal.getContactEntityListAsFlow().collect {
                state = state.copy(contactEntityList = it)
            }
        }
    }

    fun createContact(
        nickname: String,
        onSuccess: (contactEntity: ContactEntity) -> Unit
    ) {
        if (nickname.isEmpty()) return
        viewModelScope.launch {
            var newContactEntity = ContactEntity(nickname = nickname)
            val id = repositoryLocal.insertContactEntity(newContactEntity)
            newContactEntity = newContactEntity.copy(id = id)
            onSuccess(newContactEntity)
        }
    }

    fun searchContact(
        nickname: String,
        onSuccess: (contactEntityList: List<ContactEntity>) -> Unit
    ) {
        if (nickname.isEmpty()) {
            viewModelScope.launch {
                repositoryLocal.getContactEntityList()?.let{
                    state = state.copy(contactEntityList = it)
                    onSuccess(it)
                }
            }
            return
        }
        viewModelScope.launch {
            repositoryLocal.getContactEntityListSameByNickname(nickname)?.let{
                state = state.copy(contactEntityList = it)
                onSuccess(it)
            }
        }
    }

    fun deleteContact(
        contactEntity: ContactEntity
    ) {
        viewModelScope.launch {
            repositoryLocal.deleteContactEntity(contactEntity)
        }
    }

    fun updateCurrentUser(name: String) {
        state = state.copy(currentUserName = name)
        repositoryLocal.setCurrentUserName(name)
        setUiNotification("Nickname changed to $name")
    }

    fun updateCurrentContact(name: String) {
        repositoryLocal.setCurrentContactName(name)
    }
}