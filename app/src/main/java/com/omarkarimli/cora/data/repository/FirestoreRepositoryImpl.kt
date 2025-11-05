package com.omarkarimli.cora.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.omarkarimli.cora.domain.models.CategoryModel
import com.omarkarimli.cora.domain.models.CreditConditions
import com.omarkarimli.cora.domain.models.GuidelineModel
import com.omarkarimli.cora.domain.models.JournalModel
import com.omarkarimli.cora.domain.models.ReportIssueModel
import com.omarkarimli.cora.domain.models.SubscriptionModel
import com.omarkarimli.cora.domain.models.UsageDataModel
import com.omarkarimli.cora.domain.models.UserModel
import com.omarkarimli.cora.domain.repository.FirestoreRepository
import com.omarkarimli.cora.ui.theme.Durations
import com.omarkarimli.cora.utils.Constants.MONTH_IN_MILLIS
import com.omarkarimli.cora.utils.FirebaseConstants
import com.omarkarimli.cora.utils.FirebaseConstants.MONTHLY
import com.omarkarimli.cora.utils.convertDriveUrlToDirectDownload
import com.omarkarimli.cora.utils.isEarlierThan
import com.omarkarimli.cora.utils.toSubscriptionModelsList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    firestore: FirebaseFirestore
) : FirestoreRepository {

    private val _creditConditions = MutableStateFlow(CreditConditions())
    override val creditConditions: StateFlow<CreditConditions> = _creditConditions.asStateFlow()

    private val usersCollection = firestore.collection(FirebaseConstants.USERS)
    private val reportsCollection = firestore.collection(FirebaseConstants.REPORT_ISSUES)
    private val subscriptionsCollection = firestore.collection(FirebaseConstants.SUBSCRIPTIONS)
    private val guidelinesCollection = firestore.collection(FirebaseConstants.GUIDELINES)
    private val categoriesCollection = firestore.collection(FirebaseConstants.CATEGORIES)
    private val journalsCollection = firestore.collection(FirebaseConstants.JOURNALS)

    override suspend fun saveUser(userModel: UserModel) {
        val firebaseUser = auth.currentUser ?: throw IllegalStateException("User not authenticated.")

        usersCollection
            .document(firebaseUser.uid)
            .set(userModel)
            .await()
    }

    override suspend fun getUser(): UserModel? {
        var result: UserModel? = null
        var retryCount = 0
        val maxRetries = FirebaseConstants.MAX_RETRIES_GETTING_USER
        while (result == null && retryCount < maxRetries) {
            try {
                val firebaseUser = auth.currentUser ?: throw IllegalStateException("User not authenticated.")
                val documentSnapshot = usersCollection
                    .document(firebaseUser.uid)
                    .get()
                    .await()

                result = documentSnapshot.toObject(UserModel::class.java)
                _creditConditions.value = getCreditConditions(result)
            } catch (e: Exception) {
                Log.e("FirestoreRepositoryImpl", "getUser: ${e.message}")
                result = null
            }

            if (result == null) {
                delay(Durations.RETRY_DELAY)
                retryCount++
            }
        }

        return result
    }

    override suspend fun getFreeSubscriptionModels(): List<SubscriptionModel> {
        try {
            val querySnapshot = subscriptionsCollection
                .document(MONTHLY)
                .get()
                .await()
            val result = querySnapshot.toSubscriptionModelsList()

            return result.firstOrNull { it.price == 0.0 }?.let {
                listOf(
                    it.copy(
                        purchasedTime = System.currentTimeMillis(),
                        expiredTime = System.currentTimeMillis().plus(MONTH_IN_MILLIS)
                    )
                )
            } ?: emptyList()
        } catch (e: Exception) {
            Log.e("FirestoreRepositoryImpl", "getFreeSubscriptionModels: ${e.message}")
            throw e
        }
    }

    override suspend fun getSubscriptionModels(subscriptionType: String): List<SubscriptionModel> {
        try {
            val querySnapshot = subscriptionsCollection
                .document(subscriptionType)
                .get()
                .await()

            return querySnapshot.toSubscriptionModelsList()
        } catch (e: Exception) {
            Log.e("FirestoreRepositoryImpl", "getSubscriptionModels: ${e.message}")
            throw e
        }
    }

    override suspend fun getSubscriptionTypes(): List<String> {
        try {
            val querySnapshot = subscriptionsCollection
                .get()
                .await()

            val result = querySnapshot.documents.mapNotNull { it.id }.reversed()
            Log.d("FirestoreRepositoryImpl", "getSubscriptionTypes: $result")
            return result
        } catch (e: Exception) {
            Log.e("FirestoreRepositoryImpl", "getSubscriptionTypes: ${e.message}")
            throw e
        }
    }

    override suspend fun addReportIssue(reportIssueModel: ReportIssueModel) {
        try {
            reportsCollection
                .add(reportIssueModel)
                .await()
        } catch (e: Exception) {
            Log.e("FirestoreRepositoryImpl", "addReportIssue: ${e.message}")
            throw e
        }
    }

    override suspend fun getGuidelines(): List<GuidelineModel> {
        try {
            val querySnapshot = guidelinesCollection.get().await()
            return querySnapshot.toObjects(GuidelineModel::class.java)
        } catch (e: Exception) {
            Log.e("FirestoreRepositoryImpl", "getGuidelines: ${e.message}")
            throw e
        }
    }

    override suspend fun getJournals(): List<JournalModel> {
        try {
            val querySnapshot = journalsCollection.get().await()
            val result = querySnapshot.toObjects(JournalModel::class.java)
            return result.map { journalModel ->
                journalModel.copy(
                    images = journalModel.images.map { imageModel ->
                        imageModel.copy(
                            imageUrl = imageModel.imageUrl.convertDriveUrlToDirectDownload()
                        )
                    }
                )
            }
        } catch (e: Exception) {
            Log.e("FirestoreRepositoryImpl", "getJournals: ${e.message}")
            throw e
        }
    }

    override suspend fun getCategories(gender: String): List<CategoryModel> {
        try {
            val querySnapshot = categoriesCollection.get().await()
            val originalList = querySnapshot.toObjects(CategoryModel::class.java)

            val filteredList = originalList.map { categoryModel ->
                val filteredImages = categoryModel.imageModels.filter { imageModel ->
                    imageModel.gender.equals(gender, ignoreCase = true)
                }
                categoryModel.copy(
                    imageModels = filteredImages.map { imageModel ->
                        imageModel.copy(
                            imageUrl = imageModel.imageUrl.convertDriveUrlToDirectDownload()
                        )
                    }
                )
            }
            return filteredList
        } catch (e: Exception) {
            Log.e("FirestoreRepositoryImpl", "getCategories: ${e.message}")
            throw e
        }
    }

    override suspend fun updateUsageData(newUsageData: UsageDataModel) {
        try {
            val userModel = getUser() ?: throw IllegalStateException("User not authenticated.")

            val updatedUserModel = userModel.copy(usageData = newUsageData)
            saveUser(updatedUserModel)

            _creditConditions.value = getCreditConditions(updatedUserModel)
        } catch (e: Exception) {
            Log.e("FirestoreRepositoryImpl", "updateUsageData: ${e.message}")
            throw e
        }
    }

    override suspend fun getCreditConditions(userModel: UserModel?): CreditConditions {
        if (userModel == null) return CreditConditions()

        var userToProcess = userModel
        val latestSubscription = userToProcess.subscriptions.lastOrNull()
        val isCreditActive: Boolean = latestSubscription?.purchasedTime
            .isEarlierThan(latestSubscription?.expiredTime)

        if (!isCreditActive) {
            val renewedUser = renewSubscription(userToProcess)
            if (renewedUser != null) {
                userToProcess = renewedUser
            }
        }

        val finalSubscription = userToProcess.subscriptions.lastOrNull()
        val finalUsageData = userToProcess.usageData
        val finalMaxUsageData = finalSubscription?.maxUsageData ?: UsageDataModel()
        val finalIsCreditActive = finalSubscription?.purchasedTime
            .isEarlierThan(finalSubscription?.expiredTime)

        val conditions = CreditConditions(
            isCreditActive = finalIsCreditActive,
            webSearches = finalUsageData.webSearches < finalMaxUsageData.webSearches,
            attaches = finalUsageData.attaches < finalMaxUsageData.attaches,
            messageChars = finalUsageData.messageChars < finalMaxUsageData.messageChars
        )
        _creditConditions.value = conditions
        return conditions
    }

    override suspend fun renewSubscription(userModel: UserModel): UserModel? {
        try {
            val latestSubscription = userModel.subscriptions.lastOrNull()

            if (latestSubscription != null) {
                if (latestSubscription.price == 0.0) {
                    val updatedSubscriptions = userModel.subscriptions
                    val updatedUserModel = userModel.copy(
                        subscriptions = updatedSubscriptions + latestSubscription.copy(
                            purchasedTime = System.currentTimeMillis(),
                            expiredTime = System.currentTimeMillis() + MONTH_IN_MILLIS
                        )
                    )
                    saveUser(updatedUserModel)
                    return updatedUserModel
                }
            }
        } catch (e: Exception) {
            Log.e("FirestoreRepositoryImpl", "renewSubscription: ${e.message}")
            throw e
        }
        return null
    }
}
