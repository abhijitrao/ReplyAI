package com.replyai.android.data.history

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ReplyHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ReplyAiDatabase : RoomDatabase() {
    abstract fun replyHistoryDao(): ReplyHistoryDao

    companion object {
        @Volatile
        private var instance: ReplyAiDatabase? = null

        fun getInstance(context: Context): ReplyAiDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context,
                    ReplyAiDatabase::class.java,
                    "replyai.db"
                ).build().also { instance = it }
            }
    }
}
