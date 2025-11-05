package com.omarkarimli.cora.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionModel(
    val title: String = "",
    val price: Double = 0.0,
    val subscriptionType: String = "",
    val maxUsageData: UsageDataModel = UsageDataModel(),
    val adsEnabled: Boolean = true,
    val purchasedTime: Long = 0,
    val expiredTime: Long = 0
)