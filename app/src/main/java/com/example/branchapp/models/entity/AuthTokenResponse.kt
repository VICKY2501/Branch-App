package com.rishi.branchinternational.model.entity

import com.squareup.moshi.Json

// Data class representing the response containing an authentication token
data class AuthTokenResponse(
    // JSON property name for the authentication token in the response
    @Json(name = "auth_token")
    val authToken: String
)
