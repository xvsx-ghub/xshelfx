package com.xvsx.shelf.data.remote.http.response

import kotlinx.serialization.Serializable

@Serializable
data class UserValidationResponse(
    val valid: Boolean = false,
)