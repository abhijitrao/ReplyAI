package com.replyai.android.data.history

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReplyHistoryDao {
    @Query("SELECT * FROM reply_history ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<ReplyHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ReplyHistoryEntity)

    @Query("DELETE FROM reply_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM reply_history")
    suspend fun deleteAll()
}
