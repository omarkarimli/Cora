package com.omarkarimli.cora.domain.repository

interface AdminRepository {
    suspend fun setGuidelines()
    suspend fun setJournals()
    suspend fun setCategories()
    suspend fun setSubscriptionModels()
}