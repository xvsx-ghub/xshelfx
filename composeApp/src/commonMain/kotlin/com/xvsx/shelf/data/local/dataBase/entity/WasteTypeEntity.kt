package com.xvsx.shelf.data.local.dataBase.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "WasteTypeEntity")
data class WasteTypeEntity(
    val remoteId: String,
    val description: String,
    val bulkyStatus: Boolean,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
)