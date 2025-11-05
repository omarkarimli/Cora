package com.omarkarimli.cora.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.omarkarimli.cora.domain.models.CategoryModel
import com.omarkarimli.cora.domain.models.FavCategoryDetail
import com.omarkarimli.cora.domain.models.ImageModel
import kotlinx.coroutines.flow.Flow

@Dao
interface FavCategoryDetailDao {
    @Query("SELECT * FROM fav_category_details WHERE category = :category")
    suspend fun getInstance(category: CategoryModel): FavCategoryDetail?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstance(categoryDetailModel: FavCategoryDetail)

    @Query("DELETE FROM fav_category_details WHERE content_images = :contentImages")
    suspend fun deleteInstance(contentImages: List<ImageModel>)

    @Query("SELECT * FROM fav_category_details WHERE description LIKE '%' || :searchQuery || '%' ORDER BY id DESC")
    fun getPagination(searchQuery: String): PagingSource<Int, FavCategoryDetail>

    @Query("DELETE FROM fav_category_details")
    suspend fun deleteAll()

    @Query("SELECT * FROM fav_category_details ORDER BY id DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<FavCategoryDetail>>
}