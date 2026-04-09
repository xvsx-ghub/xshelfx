@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.xvsx.shelf.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

private class IosPlatformFileStorage(
    private val baseDir: NSURL,
) : PlatformFileStorage {

    override fun messagesJsonPath(): String {
        val url = baseDir.URLByAppendingPathComponent("public_chat_messages.json")!!
        return url.path!!
    }

    override fun attachmentsDirectoryPath(): String {
        val url = baseDir.URLByAppendingPathComponent("attachments", true)!!
        NSFileManager.defaultManager.createDirectoryAtURL(url, true, null, null)
        return url.path!!
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun writeBytes(relativePath: String, bytes: ByteArray) {
        val url = urlForRelative(relativePath)
        url.URLByDeletingLastPathComponent?.let { parent ->
            NSFileManager.defaultManager.createDirectoryAtURL(parent, true, null, null)
        }
        val path = url.path ?: error("Invalid path")
        writeFileBytesPosix(path, bytes)
    }

    override fun absolutePath(relativePath: String): String = urlForRelative(relativePath).path!!

    private fun urlForRelative(relativePath: String): NSURL {
        val parts = relativePath.split('/').filter { it.isNotEmpty() }
        var url = baseDir
        for (part in parts) {
            url = url.URLByAppendingPathComponent(part)!!
        }
        return url
    }
}

@Composable
actual fun rememberPlatformFileStorage(): PlatformFileStorage {
    return remember {
        val urls = NSFileManager.defaultManager.URLsForDirectory(
            NSApplicationSupportDirectory,
            NSUserDomainMask,
        )
        val appSupport = urls.firstOrNull() as NSURL
        val base = appSupport.URLByAppendingPathComponent("public_chat", true)!!
        NSFileManager.defaultManager.createDirectoryAtURL(base, true, null, null)
        IosPlatformFileStorage(base)
    }
}
