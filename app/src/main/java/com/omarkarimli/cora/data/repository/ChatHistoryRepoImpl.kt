package com.omarkarimli.cora.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.omarkarimli.cora.data.local.ChatHistoryDao
import com.omarkarimli.cora.domain.models.ChatHistoryItemModel
import com.omarkarimli.cora.domain.repository.ChatHistoryRepo
import com.omarkarimli.cora.utils.Constants
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatHistoryRepoImpl @Inject constructor(
    private val dao: ChatHistoryDao
) : ChatHistoryRepo {

    override suspend fun getInstance(id: Int): ChatHistoryItemModel? = dao.getInstance(id)

    override suspend fun insertInstance(item: ChatHistoryItemModel) {
        dao.insertInstance(item)
    }

    override fun getPagination(searchQuery: String): Flow<PagingData<ChatHistoryItemModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = Constants.MAX_PAGE_CHILD_COUNT,
                enablePlaceholders = true,
                initialLoadSize = Constants.MAX_PAGE_CHILD_COUNT
            ),
            pagingSourceFactory = { dao.getPagination(searchQuery) }
        ).flow
    }

    override suspend fun clearAll() {
        dao.deleteAll()
    }

    override fun getRecent(limit: Int): Flow<List<ChatHistoryItemModel>> = dao.getRecent(limit)

    override suspend fun deleteInstance(id: Int) {
        dao.deleteInstance(id)
    }
}