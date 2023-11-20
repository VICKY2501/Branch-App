package com.rishi.branchinternational.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rishi.branchinternational.model.entity.MessageEntity

@Database(entities = [MessageEntity::class], version = 1)
abstract class BranchInternationalRoomDatabase : RoomDatabase() {

    abstract fun branchInternationalDao(): BranchInternationalDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: BranchInternationalRoomDatabase? = null

        fun getDatabase(context: Context): BranchInternationalRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BranchInternationalRoomDatabase::class.java,
                    "message_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
