package com.omarkarimli.cora.domain.models.serper

data class WebpageResponse(
    val text: String = "",
    val metadata: Map<String, String> = emptyMap(),
    val credits: Int = 0,
    val scrapedUrl: String = "",
    val message: String? = null
)