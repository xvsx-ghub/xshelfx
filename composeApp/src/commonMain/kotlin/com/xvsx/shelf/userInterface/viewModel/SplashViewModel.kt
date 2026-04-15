package com.xvsx.shelf.userInterface.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xvsx.shelf.data.local.RepositoryLocal
import com.xvsx.shelf.data.local.dataBase.entity.ChatMessageEntity
import com.xvsx.shelf.data.remote.RepositoryRemote
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(
    private val repositoryRemote: RepositoryRemote,
    private val repositoryLocal: RepositoryLocal
) : ViewModel() {
    companion object Companion {
        const val TAG = "SplashViewModel"
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

    fun setBaseUrl(value: String) {
        repositoryLocal.setBaseUrl(value)
    }
}