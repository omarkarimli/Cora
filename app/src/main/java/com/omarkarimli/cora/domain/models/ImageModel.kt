package com.omarkarimli.cora.domain.models

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable

@Serializable
data class ImageModel(
    @ColumnInfo(name = "image_url")
    val imageUrl: String = "",
    @ColumnInfo(name = "source_url")
    val sourceUrl: String = "",
    @ColumnInfo(name = "gender")
    val gender: String = ""
)