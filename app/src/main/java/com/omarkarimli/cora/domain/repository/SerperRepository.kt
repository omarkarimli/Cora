package com.omarkarimli.cora.domain.repository

import com.omarkarimli.cora.domain.models.serper.SearchImageResponse
import com.omarkarimli.cora.domain.models.serper.WebpageResponse
import com.omarkarimli.cora.domain.models.serper.SearchTextResponse

interface SerperRepository {

    suspend fun searchText(query: String): SearchTextResponse
    suspend fun searchImage(query: String): SearchImageResponse
    suspend fun searchWebpage(url: String): WebpageResponse
}