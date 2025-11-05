package com.omarkarimli.cora.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class MessageModel(
    val text: String = "",
    val images: List<ImageModel> = emptyList(),
    val isFromMe: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val imageGeneration: Boolean = false
)