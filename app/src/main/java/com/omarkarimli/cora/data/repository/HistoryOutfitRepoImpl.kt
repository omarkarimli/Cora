package com.omarkarimli.cora.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.omarkarimli.cora.data.local.HistoryOutfitDao
import com.omarkarimli.cora.domain.models.HistoryOutfit
import com.omarkarimli.cora.domain.repository.HistoryOutfitRepo
import com.omarkarimli.cora.utils.Constants
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryOutfitRepoImpl @Inject constructor(
    private val dao: HistoryOutfitDao
) : HistoryOutfitRepo {

    override suspend fun getInstance(imagePath: String): HistoryOutfit? = dao.getInstance(imagePath)

    override suspend fun insertInstance(itemAnalysisModel: HistoryOutfit) {
        dao.insertInstance(itemAnalysisModel)
    }

    override fun getPagination(searchQuery: String): Flow<PagingData<HistoryOutfit>> {
        return Pager(
            config = PagingConfig(
                pageSize = Constants.MAX_PAGE_CHILD_COUNT,
                enablePlaceholders = true,
                initialLoadSize = Constants.MAX_PAGE_CHILD_COUNT
            ),
            pagingSourceFactory = { dao.getPaginatedItemAnalysisModelsPagingSource(searchQuery) }
        ).flow
    }

    override suspend fun clearAll() {
        dao.deleteAll()
    }

    override fun getRecent(limit: Int): Flow<List<HistoryOutfit>> = dao.getRecent(limit)

    override suspend fun deleteInstance(imagePath: String) {
        dao.deleteInstance(imagePath)
    }
}