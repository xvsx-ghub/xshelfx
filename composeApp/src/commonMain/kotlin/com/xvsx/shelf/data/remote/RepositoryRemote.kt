package com.xvsx.shelf.data.remote

import com.xvsx.shelf.data.remote.http.Http
import com.xvsx.shelf.data.remote.http.HttpClientCore.HttpStatus
import com.xvsx.shelf.data.remote.http.response.ChatMessageResponse
import com.xvsx.shelf.data.remote.http.response.UserValidationResponse

class RepositoryRemote(
    private val http: Http
) {
    companion object {
        const val TAG = "RepositoryRemote"
    }

    suspend fun getChatMessageList(
        nickname: String,
        clientName: String,
        afterId: String,
        onEvent: suspend (status: HttpStatus, data: ChatMessageResponse?, error: Exception?) -> Unit
    ) {
        http.getChatMessageList(nickname, clientName, afterId, onEvent)
    }

    suspend fun setChatMessage(
        nickname: String,
        deviceId: String,
        clientName: String,
        text: String,
        onEvent: suspend (status: HttpStatus, data: ChatMessageResponse.ChatMessage?, error: Exception?) -> Unit
    ) {
        http.setChatMessage(nickname, deviceId, clientName, text, onEvent)
    }

    suspend fun getUserValidation(
        nickname: String,
        deviceId: String,
        onEvent: suspend (status: HttpStatus, data: UserValidationResponse?, error: Exception?) -> Unit
    ) {
        http.getUserValidation(nickname, deviceId, onEvent)
    }
}