package com.omarkarimli.cora.domain.repository

import androidx.paging.PagingData
import com.omarkarimli.cora.domain.models.CategoryModel
import com.omarkarimli.cora.domain.models.FavCategoryDetail
import com.omarkarimli.cora.domain.models.ImageModel
import kotlinx.coroutines.flow.Flow

interface FavCategoryDetailRepo {

    suspend fun getInstance(category: CategoryModel): FavCategoryDetail?

    suspend fun insertInstance(categoryDetailModel: FavCategoryDetail)

    fun getPagination(searchQuery: String): Flow<PagingData<FavCategoryDetail>>

    suspend fun clearAll()

    fun getRecent(limit: Int): Flow<List<FavCategoryDetail>>
    suspend fun deleteInstance(contentImages: List<ImageModel>)
}