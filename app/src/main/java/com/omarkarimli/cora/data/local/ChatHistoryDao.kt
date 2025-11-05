package com.omarkarimli.cora.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.omarkarimli.cora.domain.models.ChatHistoryItemModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatHistoryDao {
    @Query("SELECT * FROM chat_history WHERE id = :id")
    suspend fun getInstance(id: Int): ChatHistoryItemModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstance(item: ChatHistoryItemModel)

    @Query("DELETE FROM chat_history WHERE id = :id")
    suspend fun deleteInstance(id: Int)

    @Query("SELECT * FROM chat_history WHERE title LIKE '%' || :searchQuery || '%' ORDER BY id DESC")
    fun getPagination(searchQuery: String): PagingSource<Int, ChatHistoryItemModel>

    @Query("DELETE FROM chat_history")
    suspend fun deleteAll()

    @Query("SELECT * FROM chat_history ORDER BY id DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<ChatHistoryItemModel>>
}