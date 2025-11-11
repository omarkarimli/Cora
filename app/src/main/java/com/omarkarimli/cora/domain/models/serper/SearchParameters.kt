package com.omarkarimli.cora.domain.models.serper

data class SearchParameters(
    val q: String = "",
    val type: String = "",
    val engine: String = "",
    val num: Int = 0
)