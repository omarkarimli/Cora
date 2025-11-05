package com.omarkarimli.cora.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class ClothModel(
    val imageModels: List<ImageModel> = emptyList(),
    val type: String = "",
    val material: String = "",
    val color: String = "",
    val size: String = "",
    val price: String = ""
)