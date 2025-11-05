package com.omarkarimli.cora.data.remote

import com.omarkarimli.cora.domain.models.SearchResponse
import com.omarkarimli.cora.domain.models.WebpageResponse
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Body

interface SerperApiService {

    data class SearchRequest(val q: String)
    data class WebpageRequest(val url: String)

    @POST("/images")
    suspend fun searchImage(
        @Header("X-API-KEY") apiKey: String,
        @Body requestBody: SearchRequest
    ): SearchResponse

    @POST("/scrape")
    suspend fun searchWebpage(
        @Header("X-API-KEY") apiKey: String,
        @Body requestBody: WebpageRequest
    ): WebpageResponse
}
