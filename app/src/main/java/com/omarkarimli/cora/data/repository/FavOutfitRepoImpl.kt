package com.omarkarimli.cora.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.omarkarimli.cora.data.local.FavOutfitDao
import com.omarkarimli.cora.domain.models.FavOutfit
import com.omarkarimli.cora.domain.repository.FavOutfitRepo
import com.omarkarimli.cora.utils.Constants
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavOutfitRepoImpl @Inject constructor(
    private val dao: FavOutfitDao
) : FavOutfitRepo {

    override suspend fun getInstance(imagePath: String): FavOutfit? = dao.getInstance(imagePath)

    override suspend fun insertInstance(itemAnalysisModel: FavOutfit) {
        dao.insertInstance(itemAnalysisModel)
    }

    override fun getPagination(searchQuery: String): Flow<PagingData<FavOutfit>> {
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

    override fun getRecent(limit: Int): Flow<List<FavOutfit>> = dao.getRecent(limit)

    override suspend fun deleteInstance(imagePath: String) {
        dao.deleteInstance(imagePath)
    }
}