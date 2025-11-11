package com.omarkarimli.cora.data.remote

import com.omarkarimli.cora.domain.models.serper.SearchImageResponse
import com.omarkarimli.cora.domain.models.serper.WebpageResponse
import com.omarkarimli.cora.domain.models.serper.SearchTextResponse
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Body

interface SerperApiService {

    data class TextRequest(val q: String)
    data class ImageRequest(val q: String)
    data class WebpageRequest(val url: String)

    @POST("/search")
    suspend fun searchText(
        @Header("X-API-KEY") apiKey: String,
        @Body requestBody: TextRequest
    ): SearchTextResponse

    @POST("/images")
    suspend fun searchImage(
        @Header("X-API-KEY") apiKey: String,
        @Body requestBody: ImageRequest
    ): SearchImageResponse

    @POST("/scrape")
    suspend fun searchWebpage(
        @Header("X-API-KEY") apiKey: String,
        @Body requestBody: WebpageRequest
    ): WebpageResponse
}
