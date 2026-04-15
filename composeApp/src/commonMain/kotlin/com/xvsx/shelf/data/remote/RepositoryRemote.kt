package com.xvsx.shelf.data.remote

import com.xvsx.shelf.data.remote.http.Http
import com.xvsx.shelf.data.remote.http.HttpClientCore.HttpStatus
import com.xvsx.shelf.data.remote.http.response.ChatMessageResponse

class RepositoryRemote(
    private val http: Http
) {
    companion object {
        const val TAG = "RepositoryRemote"
    }

    suspend fun getChatMessageList(onEvent: suspend (status: HttpStatus, data: ChatMessageResponse?, error: Exception?) -> Unit){
        http.getChatMessageList(onEvent)
    }
}