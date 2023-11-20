package com.example.branchapp.models.entity

import com.squareup.moshi.Json

// Data class representing a message in the messaging system
data class Message(
    // Unique identifier for the message
    @Json(name = "id")
    val id: Int,

    // Identifier for the message thread to which this message belongs
    @Json(name = "thread_id")
    val threadId: Int,

    // User ID of the sender of the message
    @Json(name = "user_id")
    val userId: String,

    // Optional agent ID if the message is sent by an agent
    @Json(name = "agent_id")
    val agentId: String?,

    // The content of the message
    @Json(name = "body")
    val messageBody: String,

    // Timestamp indicating when the message was sent
    @Json(name = "timestamp")
    val timestamp: String
)
