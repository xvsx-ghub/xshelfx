package com.xvsx.shelf.data.remote.http.response

import com.xvsx.shelf.data.local.dataBase.entity.NotServicingReasonEntity
import kotlinx.serialization.Serializable

@Serializable
data class NotServicingReasonListResponse(
    val BinNotServicingReasons: List<NotServicingReasonResponse>
)

@Serializable
data class NotServicingReasonResponse(
    val Key: String,
    val Description: String
){
    fun mapToNotServicingReasonEntity() = NotServicingReasonEntity(
        key = Key,
        description = Description
    )
}