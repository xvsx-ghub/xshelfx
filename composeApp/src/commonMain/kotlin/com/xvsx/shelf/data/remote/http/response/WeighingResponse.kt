package com.xvsx.shelf.data.remote.http.response

import kotlinx.serialization.Serializable

@Serializable
data class WeighingResponse(
    val WeighingId: Int? = null,
    val ErrorMessage: String? = null,
)
