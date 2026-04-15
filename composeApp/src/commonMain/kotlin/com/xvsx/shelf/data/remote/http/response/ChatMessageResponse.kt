package com.xvsx.shelf.data.remote.http.response

import com.xvsx.shelf.data.local.dataBase.entity.ChatMessageEntity
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageResponse(
    val messages: List<ChatMessage>? = null,
){
    @Serializable
    data class ChatMessage(
        val id: Long = -1,
        val created_at: String? = null,
        val file_url: String? = null,
        val kind: String? = null,
        val mime_type: String? = null,
        val nickname: String? = null,
        val original_name: String? = null,
        val text: String? = null
    ){
        fun mapToChatMessageEntity() = ChatMessageEntity(
            remoteId = id,
            createdAt = created_at,
            fileUrl = file_url,
            kind = kind,
            mimeType = mime_type,
            nickname = nickname,
            originalName = original_name,
            text = text
        )
    }

    fun mapToChatMessageEntityList() = messages?.map { chatMessage -> chatMessage.mapToChatMessageEntity() }
}