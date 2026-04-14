package com.xvsx.shelf.data.remote.http.response

import com.xvsx.shelf.data.local.dataBase.entity.TruckReportEntity
import kotlinx.serialization.Serializable

@Serializable
data class TruckReportListResponse(
    val TruckReportParams: List<TruckReportResponse>
)

@Serializable
data class TruckReportResponse(
    val key: String,
    val name: String
) {
    fun mapToTruckReportEntity(value: String?, photoPath: String?, checkedStatus: Boolean) =
        TruckReportEntity(
            key = key,
            name = name,
            value = value,
            photoPath = photoPath,
            checkedStatus = checkedStatus
        )
}