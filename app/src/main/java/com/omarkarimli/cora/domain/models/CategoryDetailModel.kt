package com.omarkarimli.cora.domain.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDetailModel(
    @ColumnInfo(name = "category")
    val category: CategoryModel = CategoryModel(),
    @ColumnInfo(name = "description")
    val description: String = "",
    @ColumnInfo(name = "content_images")
    val contentImages: List<ImageModel> = emptyList()
)

@Entity(
    tableName = "fav_category_details"
)
data class FavCategoryDetail(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @Embedded
    val data: CategoryDetailModel
)

@Entity(
    tableName = "history_category_details"
)
data class HistoryCategoryDetail(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @Embedded
    val data: CategoryDetailModel
)