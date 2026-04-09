package com.xvsx.shelf.chat

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import platform.posix.SEEK_END
import platform.posix.SEEK_SET
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fread
import platform.posix.fseek
import platform.posix.ftell
import platform.posix.fwrite

@OptIn(ExperimentalForeignApi::class)
internal fun readFileBytesPosixOrNull(path: String): ByteArray? {
    val file = fopen(path, "rb") ?: return null
    return try {
        fseek(file, 0, SEEK_END)
        val size = ftell(file).toInt()
        if (size < 0) return null
        fseek(file, 0, SEEK_SET)
        val buffer = ByteArray(size)
        buffer.usePinned { pinned ->
            fread(pinned.addressOf(0), 1u, size.convert(), file)
        }
        buffer
    } finally {
        fclose(file)
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun writeFileBytesPosix(path: String, bytes: ByteArray) {
    val parent = path.substringBeforeLast('/', "")
    if (parent.isNotEmpty()) {
        platform.Foundation.NSFileManager.defaultManager.createDirectoryAtPath(parent, true, null, null)
    }
    val file = fopen(path, "wb") ?: error("Cannot open $path for writing")
    try {
        bytes.usePinned { pinned ->
            fwrite(pinned.addressOf(0), 1u, bytes.size.convert(), file)
        }
    } finally {
        fclose(file)
    }
}
