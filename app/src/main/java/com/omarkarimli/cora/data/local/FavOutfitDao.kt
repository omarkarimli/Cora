package com.omarkarimli.cora.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.omarkarimli.cora.domain.models.FavOutfit
import kotlinx.coroutines.flow.Flow

@Dao
interface FavOutfitDao {
    @Query("SELECT * FROM fav_outfits WHERE image_path = :imagePath")
    suspend fun getInstance(imagePath: String): FavOutfit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstance(itemAnalysisModel: FavOutfit)

    @Query("DELETE FROM fav_outfits WHERE image_path = :imagePath")
    suspend fun deleteInstance(imagePath: String)

    @Query("SELECT * FROM fav_outfits WHERE title LIKE '%' || :searchQuery || '%' ORDER BY id DESC")
    fun getPagination(searchQuery: String): PagingSource<Int, FavOutfit>

    @Query("DELETE FROM fav_outfits")
    suspend fun deleteAll()

    @Query("SELECT * FROM fav_outfits ORDER BY id DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<FavOutfit>>
}