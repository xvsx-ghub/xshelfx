package com.xvsx.shelf.data.remote.stomp

import com.xvsx.shelf.data.local.RepositoryLocal
import com.xvsx.shelf.util.System

expect class StompManager (repositoryLocal: RepositoryLocal, system: System){
    fun start(stompConnectionDetails: StompConnectionDetails): Boolean
    fun stop(): Boolean
    suspend fun collectStompEvent(onEvent: (Stomp.Event) -> Unit)
    fun getLastStompEvent(): Stomp.Event?
    suspend fun collectStompData(onMessage: suspend (StompMessage) -> Unit)
}