package com.omarkarimli.cora.domain.models.serper

data class SearchImageResponse(
    val searchParameters: SearchParameters = SearchParameters(),
    val images: List<ImageResult> = emptyList(),
    val credits: Int = 0
)

data class ImageResult(
    val title: String = "",
    val imageUrl: String = "",
    val imageWidth: Int = 0,
    val imageHeight: Int = 0,
    val thumbnailUrl: String = "",
    val thumbnailWidth: Int = 0,
    val thumbnailHeight: Int = 0,
    val source: String = "",
    val domain: String = "",
    val link: String = "",
    val googleUrl: String = "",
    val position: Int = 0
)