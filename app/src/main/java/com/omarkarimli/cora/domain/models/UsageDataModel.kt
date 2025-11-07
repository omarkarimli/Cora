package com.omarkarimli.cora.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class UsageDataModel(
    val attaches: Int = 0,
    val messageChars: Int = 0,
    val webSearchResultCount: Int = 0
)