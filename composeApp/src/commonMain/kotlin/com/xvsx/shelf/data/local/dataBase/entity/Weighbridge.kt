package com.xvsx.shelf.data.local.dataBase.entity

import kotlinx.serialization.Serializable

@Serializable
data class Weighbridge(
    val remoteId: String,
    val name: String,
    val type: String,
    val canWeighStatus: Boolean,
    val weightUnit: String,
)