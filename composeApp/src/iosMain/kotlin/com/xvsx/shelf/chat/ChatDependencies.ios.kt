package com.xvsx.shelf.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberChatRepository(): ChatRepository {
    val storage = rememberPlatformFileStorage()
    return remember(storage) { ChatRepository(storage) }
}
