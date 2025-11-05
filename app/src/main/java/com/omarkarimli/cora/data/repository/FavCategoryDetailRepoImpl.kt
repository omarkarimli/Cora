package com.omarkarimli.cora.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.omarkarimli.cora.data.local.FavCategoryDetailDao
import com.omarkarimli.cora.domain.models.CategoryModel
import com.omarkarimli.cora.domain.models.FavCategoryDetail
import com.omarkarimli.cora.domain.models.ImageModel
import com.omarkarimli.cora.domain.repository.FavCategoryDetailRepo
import com.omarkarimli.cora.utils.Constants
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavCategoryDetailRepoImpl @Inject constructor(
    private val dao: FavCategoryDetailDao
) : FavCategoryDetailRepo {

    override suspend fun getInstance(category: CategoryModel): FavCategoryDetail? = dao.getInstance(category)

    override suspend fun insertInstance(categoryDetailModel: FavCategoryDetail) {
        dao.insertInstance(categoryDetailModel)
    }

    override fun getPagination(searchQuery: String): Flow<PagingData<FavCategoryDetail>> {
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

    override fun getRecent(limit: Int): Flow<List<FavCategoryDetail>> = dao.getRecent(limit)

    override suspend fun deleteInstance(contentImages: List<ImageModel>) {
        dao.deleteInstance(contentImages)
    }
}