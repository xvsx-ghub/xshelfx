package com.xvsx.shelf.data.local.dataBase.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

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
) {
    fun getIsoTimestamp(): String {
        createdAt?.let {
            val instant = Instant.parse(it)
            val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())

            return "${local.date} ${
                local.hour.toString().padStart(2, '0')
            }:${local.minute.toString().padStart(2, '0')}"
        }
        return ""
    }
}