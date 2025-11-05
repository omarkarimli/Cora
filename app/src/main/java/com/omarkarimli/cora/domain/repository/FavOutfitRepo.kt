package com.omarkarimli.cora.domain.repository

import androidx.paging.PagingData
import com.omarkarimli.cora.domain.models.FavOutfit
import kotlinx.coroutines.flow.Flow

interface FavOutfitRepo {
    suspend fun getInstance(imagePath: String): FavOutfit?

    suspend fun deleteInstance(imagePath: String)

    suspend fun insertInstance(itemAnalysisModel: FavOutfit)

    fun getPagination(searchQuery: String): Flow<PagingData<FavOutfit>>

    suspend fun clearAll()

    fun getRecent(limit: Int): Flow<List<FavOutfit>>
}