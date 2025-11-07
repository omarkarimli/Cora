package com.omarkarimli.cora.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.omarkarimli.cora.domain.models.GuidelineModel
import com.omarkarimli.cora.domain.models.SubscriptionModel
import com.omarkarimli.cora.domain.models.UsageDataModel
import com.omarkarimli.cora.domain.repository.AdminRepository
import com.omarkarimli.cora.utils.FirebaseConstants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AdminRepositoryImpl @Inject constructor(
    firestore: FirebaseFirestore
) : AdminRepository {
    private val subscriptionsCollection = firestore.collection(FirebaseConstants.SUBSCRIPTIONS)
    private val guidelinesCollection = firestore.collection(FirebaseConstants.GUIDELINES)

    override suspend fun setSubscriptionModels() {
        val subscriptionModelsM = listOf(
            SubscriptionModel(
                title = "Basic",
                price = 0.0,
                subscriptionType = FirebaseConstants.MONTHLY,
                maxUsageData = UsageDataModel(
                    attaches = 20,
                    messageChars = 300,
                    webSearchResultCount = 3
                ),
                adsEnabled = true
            ),
            SubscriptionModel(
                title = "Plus",
                price = 19.99,
                subscriptionType = FirebaseConstants.MONTHLY,
                maxUsageData = UsageDataModel(
                    attaches = 30,
                    messageChars = 900,
                    webSearchResultCount = 5
                ),
                adsEnabled = false
            ),
            SubscriptionModel(
                title = "Pro",
                price = 39.99,
                subscriptionType = FirebaseConstants.MONTHLY,
                maxUsageData = UsageDataModel(
                    attaches = 40,
                    messageChars = 1500,
                    webSearchResultCount = 7
                ),
                adsEnabled = false
            )
        )
        val subscriptionModelsA = listOf(
            SubscriptionModel(
                title = "Plus",
                price = 229.99,
                subscriptionType = FirebaseConstants.ANNUAL,
                maxUsageData = UsageDataModel(
                    attaches = 360,
                    messageChars = 10800,
                    webSearchResultCount = 5
                ),
                adsEnabled = false
            ),
            SubscriptionModel(
                title = "Pro",
                price = 459.99,
                subscriptionType = FirebaseConstants.ANNUAL,
                maxUsageData = UsageDataModel(
                    attaches = 480,
                    messageChars = 18000,
                    webSearchResultCount = 7
                ),
                adsEnabled = false
            )
        )

        // Create a Map to store the monthly subscriptions
        val monthlyData = hashMapOf(
            FirebaseConstants.SUBSCRIPTIONS to subscriptionModelsM
        )

        // Create a Map to store the annual subscriptions
        val annualData = hashMapOf(
            FirebaseConstants.SUBSCRIPTIONS to subscriptionModelsA
        )

        try {
            subscriptionsCollection
                .document(FirebaseConstants.MONTHLY)
                .set(monthlyData)
                .await()

            subscriptionsCollection
                .document(FirebaseConstants.ANNUAL)
                .set(annualData)
                .await()
        } catch (e: Exception) {
            Log.e("FirestoreRepositoryImpl", "setSubscriptionModels: ${e.message}")
            throw e
        }
    }
    override suspend fun setGuidelines() {
        val list = listOf(
            GuidelineModel(
                id = 0,
                title = "Use Responsibly",
                description = "Do not use AI to generate harmful, misleading, or illegal content."
            ),
            GuidelineModel(
                id = 1,
                title = "Respect Privacy",
                description = "Do not share or process private, sensitive, or personal information without consent."
            ),
            GuidelineModel(
                id = 2,
                title = "Fair Usage",
                description = "Avoid excessive or abusive use of AI features beyond the allowed limits."
            ),
            GuidelineModel(
                id = 3,
                title = "No Harmful Activities",
                description = "Do not use AI for harassment, hate speech, self-harm promotion, or spreading violence."
            ),
            GuidelineModel(
                id = 4,
                title = "Accuracy Awareness",
                description = "AI outputs may be incorrect or biased. Always verify critical information before use."
            )
        )

        try {
            for (item in list) {
                guidelinesCollection
                    .document(item.id.toString())
                    .set(item)
                    .await()
            }
            Log.d("FirestoreRepositoryImpl", "Successfully set guidelines.")
        } catch (e: Exception) {
            Log.e("FirestoreRepositoryImpl", "setGuidelines: ${e.message}")
        }
    }
}
