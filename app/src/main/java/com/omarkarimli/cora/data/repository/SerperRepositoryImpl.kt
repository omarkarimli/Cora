package com.omarkarimli.cora.data.repository

import android.util.Log
import com.omarkarimli.cora.BuildConfig.SERPER_API_KEY
import javax.inject.Inject
import javax.inject.Singleton
import com.omarkarimli.cora.data.remote.SerperApiService
import com.omarkarimli.cora.domain.models.SearchImageResponse
import com.omarkarimli.cora.domain.models.WebpageResponse
import com.omarkarimli.cora.domain.models.serper.SearchTextResponse
import com.omarkarimli.cora.domain.repository.SerperRepository

@Singleton
class SerperRepositoryImpl @Inject constructor(
    private val serperApiService: SerperApiService
) : SerperRepository {

    override suspend fun searchText(query: String): SearchTextResponse {
        return try {
            val requestBody = SerperApiService.TextRequest(q = query)
            val response = serperApiService.searchText(
                apiKey = SERPER_API_KEY,
                requestBody = requestBody
            )

            response
        } catch (e: Exception) {
            Log.e("SerperRepositoryImpl", "Error in text search: ${e.message}", e)
            SearchTextResponse()
        }
    }

    override suspend fun searchImage(query: String): SearchImageResponse {
        return try {
            val requestBody = SerperApiService.ImageRequest(q = query)
            val response = serperApiService.searchImage(
                apiKey = SERPER_API_KEY,
                requestBody = requestBody
            )

            response
        } catch (e: Exception) {
            Log.e("SerperRepositoryImpl", "Error in image search: ${e.message}", e)
            SearchImageResponse()
        }
    }

    override suspend fun searchWebpage(url: String): WebpageResponse {
        return try {
            val requestBody = SerperApiService.WebpageRequest(url = url)
            val response = serperApiService.searchWebpage(
                apiKey = SERPER_API_KEY,
                requestBody = requestBody
            )
            response
        } catch (e: Exception) {
            Log.e("SerperRepositoryImpl", "Error in webpage search:", e)
            WebpageResponse()
        }
    }
}
