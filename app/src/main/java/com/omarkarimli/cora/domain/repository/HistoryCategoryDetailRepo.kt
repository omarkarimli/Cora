package com.omarkarimli.cora.domain.repository

import androidx.paging.PagingData
import com.omarkarimli.cora.domain.models.CategoryModel
import com.omarkarimli.cora.domain.models.HistoryCategoryDetail
import com.omarkarimli.cora.domain.models.ImageModel
import kotlinx.coroutines.flow.Flow

interface HistoryCategoryDetailRepo {

    suspend fun getInstance(category: CategoryModel): HistoryCategoryDetail?

    suspend fun insertInstance(categoryDetailModel: HistoryCategoryDetail)

    fun getPagination(searchQuery: String): Flow<PagingData<HistoryCategoryDetail>>

    suspend fun clearAll()

    fun getRecent(limit: Int): Flow<List<HistoryCategoryDetail>>
    suspend fun deleteInstance(contentImages: List<ImageModel>)
}