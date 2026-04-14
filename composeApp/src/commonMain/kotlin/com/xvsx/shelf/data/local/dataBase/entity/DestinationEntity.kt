package com.xvsx.shelf.data.local.dataBase.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "DestinationEntity")
data class DestinationEntity(
    val remoteId: String,
    val description: String,
    val lat: String,
    val lng: String,
    val weighbridgeList: List<Weighbridge>,
    val weightUnit: String,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
)