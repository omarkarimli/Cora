package com.omarkarimli.cora.domain.models

data class CreditConditions(
    val isCreditActive: Boolean = false,
    val webSearches: Boolean = false,
    val attaches: Boolean = false,
    val messageChars: Boolean = false
)