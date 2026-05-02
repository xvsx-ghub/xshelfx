package com.xvsx.shelf.data.remote.http.response

import kotlinx.serialization.Serializable

@Serializable
data class FcmRegisterResponse(
    val ok: Boolean? = null,
    val error: String? = null,
    val fcm_ready: Boolean? = null,
)
