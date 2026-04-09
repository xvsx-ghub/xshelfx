package com.xvsx.shelf.chat

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import platform.Foundation.NSFileManager
import platform.Foundation.NSUUID
import platform.posix.CLOCK_REALTIME
import platform.posix.clock_gettime
import platform.posix.timespec

@OptIn(ExperimentalForeignApi::class)
internal actual fun currentTimeMillis(): Long = memScoped {
    val ts = alloc<timespec>()
    if (clock_gettime(CLOCK_REALTIME.convert(), ts.ptr) != 0) return@memScoped 0L
    ts.tv_sec * 1000L + ts.tv_nsec / 1_000_000L
}

internal actual fun newId(): String = NSUUID().UUIDString

internal actual fun readFileBytesOrNull(absolutePath: String): ByteArray? {
    if (!NSFileManager.defaultManager.fileExistsAtPath(absolutePath)) return null
    return readFileBytesPosixOrNull(absolutePath)
}

internal actual fun writeFileBytes(absolutePath: String, bytes: ByteArray) {
    writeFileBytesPosix(absolutePath, bytes)
}
