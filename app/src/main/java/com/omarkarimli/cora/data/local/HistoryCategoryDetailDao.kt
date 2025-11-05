package com.omarkarimli.cora.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.omarkarimli.cora.domain.models.CategoryModel
import com.omarkarimli.cora.domain.models.HistoryCategoryDetail
import com.omarkarimli.cora.domain.models.ImageModel
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryCategoryDetailDao {
    @Query("SELECT * FROM history_category_details WHERE category = :category")
    suspend fun getInstance(category: CategoryModel): HistoryCategoryDetail?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstance(categoryDetailModel: HistoryCategoryDetail)

    @Query("DELETE FROM history_category_details WHERE content_images = :contentImages")
    suspend fun deleteInstance(contentImages: List<ImageModel>)

    @Query("SELECT * FROM history_category_details WHERE description LIKE '%' || :searchQuery || '%' ORDER BY id DESC")
    fun getPagination(searchQuery: String): PagingSource<Int, HistoryCategoryDetail>

    @Query("DELETE FROM history_category_details")
    suspend fun deleteAll()

    @Query("SELECT * FROM history_category_details ORDER BY id DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<HistoryCategoryDetail>>
}