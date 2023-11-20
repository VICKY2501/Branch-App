package com.rishi.branchinternational.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rishi.branchinternational.model.entity.MessageEntity

@Dao
interface BranchInternationalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMessagesToDataBase(messages: List<MessageEntity>)

    @Query("SELECT * FROM MESSAGE_TABLE where thread_id= :threadId")
    suspend fun getMessagesOnAThread(threadId: String): List<MessageEntity>
}
