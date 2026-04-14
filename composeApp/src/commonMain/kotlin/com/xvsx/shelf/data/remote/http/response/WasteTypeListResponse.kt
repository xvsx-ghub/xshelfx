package com.xvsx.shelf.data.remote.http.response

import com.xvsx.shelf.data.local.dataBase.entity.WasteTypeEntity
import kotlinx.serialization.Serializable

@Serializable
data class WasteTypeListResponse(
    val WasteTypes: List<WasteTypeResponse>? = null
)

@Serializable
data class WasteTypeResponse(
    val Id: String,
    val Description: String,
    val IsBulky: Boolean,
) {
    fun mapToWasteTypeEntity() = WasteTypeEntity(Id, Description, IsBulky)
}