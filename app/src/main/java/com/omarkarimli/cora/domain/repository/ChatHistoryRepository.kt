package com.omarkarimli.cora.domain.repository

import androidx.paging.PagingData
import com.omarkarimli.cora.domain.models.ChatHistoryItemModel
import kotlinx.coroutines.flow.Flow

interface ChatHistoryRepository {
    suspend fun getInstance(id: Int): ChatHistoryItemModel?

    suspend fun deleteInstance(id: Int)

    suspend fun insertInstance(item: ChatHistoryItemModel)

    fun getPagination(searchQuery: String): Flow<PagingData<ChatHistoryItemModel>>

    suspend fun clearAll()

    fun getRecent(limit: Int): Flow<List<ChatHistoryItemModel>>
}