package com.rishi.branchinternational.model.entity

import com.squareup.moshi.Json

// Data class representing a message post request for API communication
data class MessagePostRequest(
    // Identifier for the message thread to which the message belongs
    @Json(name = "thread_id")
    val threadId: Int,

    // The content of the message
    @Json(name = "body")
    val body: String
)
