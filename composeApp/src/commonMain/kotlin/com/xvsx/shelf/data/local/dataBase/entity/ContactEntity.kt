package com.xvsx.shelf.data.local.dataBase.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ContactEntity")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nickname: String? = null,
)