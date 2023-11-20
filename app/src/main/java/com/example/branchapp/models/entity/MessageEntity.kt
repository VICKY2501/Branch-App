package com.rishi.branchinternational.model.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
// Define the Table name
@Entity(tableName = "message_table")
data class MessageEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo val body: String,
    @ColumnInfo val timestamp: String,
    // Specifies the name of the column in the table if you want it to be different from the name of the member variable.
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "agent_id") val agentId: String?,
    @ColumnInfo(name = "thread_id") var threadId: Int
) : Parcelable
