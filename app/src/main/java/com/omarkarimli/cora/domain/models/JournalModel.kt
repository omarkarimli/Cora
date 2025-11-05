package com.omarkarimli.cora.domain.models

data class JournalModel(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val images: List<ImageModel> = emptyList()
)