package com.omarkarimli.cora.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.omarkarimli.cora.domain.models.HistoryOutfit
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryOutfitDao {
    @Query("SELECT * FROM history_outfits WHERE image_path = :imagePath")
    suspend fun getInstance(imagePath: String): HistoryOutfit?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertInstance(itemAnalysisModel: HistoryOutfit)

    @Query("DELETE FROM history_outfits WHERE image_path = :imagePath")
    suspend fun deleteInstance(imagePath: String)

    @Query("SELECT * FROM history_outfits WHERE title LIKE '%' || :searchQuery || '%' ORDER BY id DESC")
    fun getPaginatedItemAnalysisModelsPagingSource(searchQuery: String): PagingSource<Int, HistoryOutfit>

    @Query("DELETE FROM history_outfits")
    suspend fun deleteAll()

    @Query("SELECT * FROM history_outfits ORDER BY id DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<HistoryOutfit>>
}