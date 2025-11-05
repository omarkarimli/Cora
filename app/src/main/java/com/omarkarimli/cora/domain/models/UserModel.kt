package com.omarkarimli.cora.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class UserModel (
    val idToken: String = "",
    val personalInfo: PersonalInfoModel = PersonalInfoModel(),
    val subscriptions: List<SubscriptionModel> = emptyList(),
    val usageData: UsageDataModel = UsageDataModel()
)