package com.replyai.android.data.history

import com.replyai.android.domain.model.ReplyHistoryItem
import com.replyai.android.domain.repository.ReplyHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomReplyHistoryRepository(
    private val dao: ReplyHistoryDao
) : ReplyHistoryRepository {

    override fun observeHistory(): Flow<List<ReplyHistoryItem>> =
        dao.observeAll().map { items -> items.map(ReplyHistoryEntity::toDomain) }

    override suspend fun save(item: ReplyHistoryItem) {
        dao.insert(item.toEntity())
    }

    override suspend fun delete(id: Long) {
        dao.deleteById(id)
    }

    override suspend fun clear() {
        dao.deleteAll()
    }
}
