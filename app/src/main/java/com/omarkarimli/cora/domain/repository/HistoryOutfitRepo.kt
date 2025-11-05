package com.omarkarimli.cora.domain.repository

import androidx.paging.PagingData
import com.omarkarimli.cora.domain.models.HistoryOutfit
import kotlinx.coroutines.flow.Flow

interface HistoryOutfitRepo {
    suspend fun getInstance(imagePath: String): HistoryOutfit?

    suspend fun deleteInstance(imagePath: String)

    suspend fun insertInstance(itemAnalysisModel: HistoryOutfit)

    fun getPagination(searchQuery: String): Flow<PagingData<HistoryOutfit>>

    suspend fun clearAll()

    fun getRecent(limit: Int): Flow<List<HistoryOutfit>>
}