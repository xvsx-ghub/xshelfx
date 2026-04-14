package com.xvsx.shelf.data.local.dataBase.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "NotServicingReasonEntity")
data class NotServicingReasonEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val key: String,
    val description: String
)