package com.omarkarimli.cora.domain.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
data class ItemAnalysisModel(
    @ColumnInfo(name = "image_path")
    val imagePath: String = "",
    @ColumnInfo(name = "title")
    val title: String = "",
    @ColumnInfo(name = "description")
    val description: String = "",
    @ColumnInfo(name = "gender")
    val gender: String = "",
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "parts")
    val parts: List<ClothModel> = emptyList(),
    @ColumnInfo(name = "recommended_looks")
    val recommendedLooks: List<ImageModel> = emptyList()
)

@Entity(
    tableName = "fav_outfits", // 🔑 Unique Table Name
    indices = [Index(value = ["image_path"], unique = true)]
)
data class FavOutfit(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @Embedded
    val data: ItemAnalysisModel
)

@Entity(
    tableName = "history_outfits", // 🔑 Unique Table Name
    indices = [Index(value = ["image_path"], unique = true)]
)
data class HistoryOutfit(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @Embedded
    val data: ItemAnalysisModel
)