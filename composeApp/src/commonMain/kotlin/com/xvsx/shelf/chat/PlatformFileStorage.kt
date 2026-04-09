package com.xvsx.shelf.chat

import androidx.compose.runtime.Composable

/**
 * App-private storage for chat JSON and attachment files.
 */
interface PlatformFileStorage {
    fun messagesJsonPath(): String

    fun attachmentsDirectoryPath(): String

    fun writeBytes(relativePath: String, bytes: ByteArray)

    fun absolutePath(relativePath: String): String
}

@Composable
expect fun rememberPlatformFileStorage(): PlatformFileStorage
