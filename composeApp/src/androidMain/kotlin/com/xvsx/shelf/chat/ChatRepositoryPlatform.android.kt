package com.xvsx.shelf.chat

import java.io.File
import java.util.UUID

internal actual fun currentTimeMillis(): Long = System.currentTimeMillis()

internal actual fun newId(): String = UUID.randomUUID().toString()

internal actual fun readFileBytesOrNull(absolutePath: String): ByteArray? {
    val f = File(absolutePath)
    if (!f.exists() || !f.isFile) return null
    return runCatching { f.readBytes() }.getOrNull()
}

internal actual fun writeFileBytes(absolutePath: String, bytes: ByteArray) {
    val f = File(absolutePath)
    f.parentFile?.mkdirs()
    f.writeBytes(bytes)
}
