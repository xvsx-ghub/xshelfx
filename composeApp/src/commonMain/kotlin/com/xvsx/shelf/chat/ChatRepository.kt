package com.xvsx.shelf.chat

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

private const val ATTACHMENTS_DIR = "attachments"

class ChatRepository(
    private val storage: PlatformFileStorage,
) {
    suspend fun loadMessages(): List<ChatMessage> = withContext(Dispatchers.Default) {
        val path = storage.messagesJsonPath()
        val file = readFileIfExists(path) ?: return@withContext emptyList()
        runCatching {
            json.decodeFromString<List<ChatMessage>>(file.decodeToString())
        }.getOrElse { emptyList() }
    }

    suspend fun sendText(text: String): ChatMessage = withContext(Dispatchers.Default) {
        val trimmed = text.trim()
        require(trimmed.isNotEmpty()) { "Message cannot be empty" }
        val message = ChatMessage(
            id = newId(),
            timestampMillis = currentTimeMillis(),
            text = trimmed,
        )
        appendMessage(message)
        message
    }

    suspend fun sendAttachment(
        bytes: ByteArray,
        mimeType: String,
        originalName: String?,
    ): ChatMessage = withContext(Dispatchers.Default) {
        require(bytes.isNotEmpty()) { "Empty file" }
        val kind = guessKind(mimeType, originalName)
        val ext = guessExtension(mimeType, originalName, kind)
        val relative = "$ATTACHMENTS_DIR/${newId()}$ext"
        storage.writeBytes(relative, bytes)
        val message = ChatMessage(
            id = newId(),
            timestampMillis = currentTimeMillis(),
            text = "",
            attachment = StoredAttachment(
                kind = kind,
                relativePath = relative,
                originalName = originalName,
                mimeType = mimeType.ifBlank { "application/octet-stream" },
            ),
        )
        appendMessage(message)
        message
    }

    private fun appendMessage(message: ChatMessage) {
        val path = storage.messagesJsonPath()
        val existing = runCatching {
            readFileIfExists(path)?.decodeToString()?.let { json.decodeFromString<List<ChatMessage>>(it) }
        }.getOrNull().orEmpty()
        val next = existing + message
        writeFile(path, json.encodeToString(next).encodeToByteArray())
    }

    private fun readFileIfExists(absolutePath: String): ByteArray? =
        readFileBytesOrNull(absolutePath)

    private fun writeFile(absolutePath: String, bytes: ByteArray) {
        writeFileBytes(absolutePath, bytes)
    }
}

internal expect fun currentTimeMillis(): Long

internal expect fun newId(): String

internal expect fun readFileBytesOrNull(absolutePath: String): ByteArray?

internal expect fun writeFileBytes(absolutePath: String, bytes: ByteArray)

private fun guessKind(mime: String, name: String?): AttachmentKind {
    val m = mime.lowercase()
    val n = name?.lowercase().orEmpty()
    return when {
        m.startsWith("image/") || n.endsWith(".jpg") || n.endsWith(".jpeg") || n.endsWith(".png") ||
            n.endsWith(".gif") || n.endsWith(".webp") || n.endsWith(".heic")
        -> AttachmentKind.IMAGE

        m.startsWith("video/") || n.endsWith(".mp4") || n.endsWith(".mov") || n.endsWith(".webm") ||
            n.endsWith(".mkv")
        -> AttachmentKind.VIDEO

        m.startsWith("audio/") || n.endsWith(".mp3") || n.endsWith(".aac") || n.endsWith(".m4a") ||
            n.endsWith(".wav") || n.endsWith(".ogg")
        -> AttachmentKind.AUDIO

        else -> AttachmentKind.IMAGE
    }
}

private fun guessExtension(mime: String, name: String?, kind: AttachmentKind): String {
    name?.substringAfterLast('.', "")?.takeIf { it.length in 1..8 }?.let { return ".$it" }
    val m = mime.lowercase()
    return when {
        m.contains("jpeg") -> ".jpg"
        m.contains("png") -> ".png"
        m.contains("gif") -> ".gif"
        m.contains("webp") -> ".webp"
        m.contains("mp4") -> ".mp4"
        m.contains("quicktime") || m.contains("mov") -> ".mov"
        m.contains("mpeg") || m.contains("mp3") -> ".mp3"
        m.contains("wav") -> ".wav"
        m.contains("aac") -> ".aac"
        m.contains("m4a") -> ".m4a"
        else -> when (kind) {
            AttachmentKind.IMAGE -> ".jpg"
            AttachmentKind.VIDEO -> ".mp4"
            AttachmentKind.AUDIO -> ".m4a"
        }
    }
}
