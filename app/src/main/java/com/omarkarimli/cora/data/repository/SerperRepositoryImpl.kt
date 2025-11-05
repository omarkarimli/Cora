package com.omarkarimli.cora.data.repository

import android.util.Log
import com.omarkarimli.cora.BuildConfig.SERPER_API_KEY
import javax.inject.Inject
import javax.inject.Singleton
import com.omarkarimli.cora.data.remote.SerperApiService
import com.omarkarimli.cora.domain.models.SearchResponse
import com.omarkarimli.cora.domain.models.WebpageResponse
import com.omarkarimli.cora.domain.repository.FirestoreRepository
import com.omarkarimli.cora.domain.repository.SerperRepository

@Singleton
class SerperRepositoryImpl @Inject constructor(
    private val serperApiService: SerperApiService,
    private val firestoreRepository: FirestoreRepository
) : SerperRepository {

    override suspend fun searchImage(query: String): SearchResponse {
        return try {
            val requestBody = SerperApiService.SearchRequest(q = query)
            val response = serperApiService.searchImage(
                apiKey = SERPER_API_KEY,
                requestBody = requestBody
            )

            response
        } catch (e: Exception) {
            Log.e("SerperRepositoryImpl", "Error in image search: ${e.message}", e)
            SearchResponse()
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
