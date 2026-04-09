package com.xvsx.shelf.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.io.File

private const val BASE = "public_chat"

private class AndroidPlatformFileStorage(
    private val base: File,
) : PlatformFileStorage {

    override fun messagesJsonPath(): String = File(base, "public_chat_messages.json").absolutePath

    override fun attachmentsDirectoryPath(): String =
        File(base, "attachments").apply { mkdirs() }.absolutePath

    override fun writeBytes(relativePath: String, bytes: ByteArray) {
        val f = File(base, relativePath)
        f.parentFile?.mkdirs()
        f.writeBytes(bytes)
    }

    override fun absolutePath(relativePath: String): String = File(base, relativePath).absolutePath
}

@Composable
actual fun rememberPlatformFileStorage(): PlatformFileStorage {
    val context = LocalContext.current.applicationContext
    return remember(context) {
        val root = File(context.filesDir, BASE).apply { mkdirs() }
        AndroidPlatformFileStorage(root)
    }
}
