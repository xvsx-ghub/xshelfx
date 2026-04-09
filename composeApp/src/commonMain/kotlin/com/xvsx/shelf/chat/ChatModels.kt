package com.xvsx.shelf.chat

import kotlinx.serialization.Serializable

@Serializable
enum class AttachmentKind {
    IMAGE,
    VIDEO,
    AUDIO,
}

@Serializable
data class StoredAttachment(
    val kind: AttachmentKind,
    /** Path relative to app storage (e.g. attachments/uuid.jpg) */
    val relativePath: String,
    val originalName: String? = null,
    val mimeType: String,
)

@Serializable
data class ChatMessage(
    val id: String,
    val timestampMillis: Long,
    val text: String,
    val attachment: StoredAttachment? = null,
)
