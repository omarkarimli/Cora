package com.omarkarimli.cora.domain.repository

interface AdminRepository {
    suspend fun setGuidelines()
    suspend fun setSubscriptionModels()
}