package com.rishi.branchinternational.model.database

import androidx.annotation.WorkerThread
import com.rishi.branchinternational.model.entity.MessageEntity

class BranchInternationalRepository(private val branchInternationalDao: BranchInternationalDao) {

    @WorkerThread
    suspend fun cacheAllMessagesToDataBase(messages: List<MessageEntity>) {
        branchInternationalDao.insertAllMessagesToDataBase(messages)
    }

}
