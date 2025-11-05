package com.omarkarimli.cora.domain.repository

import com.omarkarimli.cora.domain.models.SearchResponse
import com.omarkarimli.cora.domain.models.WebpageResponse

interface SerperRepository {

    suspend fun searchImage(query: String): SearchResponse
    suspend fun searchWebpage(url: String): WebpageResponse
}