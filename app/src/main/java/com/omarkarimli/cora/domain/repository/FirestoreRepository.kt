package com.omarkarimli.cora.domain.repository

import com.omarkarimli.cora.domain.models.CreditConditions
import com.omarkarimli.cora.domain.models.GuidelineModel
import com.omarkarimli.cora.domain.models.ReportIssueModel
import com.omarkarimli.cora.domain.models.SubscriptionModel
import com.omarkarimli.cora.domain.models.UsageDataModel
import com.omarkarimli.cora.domain.models.UserModel
import kotlinx.coroutines.flow.StateFlow

interface FirestoreRepository {
    val creditConditions: StateFlow<CreditConditions>

    suspend fun saveUser(userModel: UserModel)
    suspend fun getUser(): UserModel?
    suspend fun getFreeSubscriptionModels(): List<SubscriptionModel>
    suspend fun getSubscriptionModels(subscriptionType: String): List<SubscriptionModel>
    suspend fun getSubscriptionTypes(): List<String>
    suspend fun addReportIssue(reportIssueModel: ReportIssueModel)
    suspend fun getGuidelines(): List<GuidelineModel>
    suspend fun updateUsageData(newUsageData: UsageDataModel)
    suspend fun getCreditConditions(userModel: UserModel?): CreditConditions
    suspend fun renewSubscription(userModel: UserModel): UserModel?
}