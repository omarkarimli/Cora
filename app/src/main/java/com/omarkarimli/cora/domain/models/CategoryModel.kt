package com.omarkarimli.cora.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class CategoryModel(
    val id: Int = 0,
    val title: String = "",
    val subtitle: String = "",
    val imageModels: List<ImageModel> = emptyList()
)