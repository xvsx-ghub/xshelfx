package com.xvsx.shelf.data.local.dataBase.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ChatMessageEntity")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val remoteId: Long = -1,
    val createdAt: String? = null,
    val fileUrl: String? = null,
    val kind: String? = null,
    val mimeType: String? = null,
    val nickname: String? = null,
    val originalName: String? = null,
    val text: String? = null
)