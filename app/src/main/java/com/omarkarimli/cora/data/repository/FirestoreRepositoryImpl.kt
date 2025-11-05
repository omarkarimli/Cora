package com.omarkarimli.cora.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.omarkarimli.cora.domain.models.CategoryModel
import com.omarkarimli.cora.domain.models.CreditConditions
import com.omarkarimli.cora.domain.models.GuidelineModel
import com.omarkarimli.cora.domain.models.ImageModel
import com.omarkarimli.cora.domain.models.JournalModel
import com.omarkarimli.cora.domain.models.ReportIssueModel
import com.omarkarimli.cora.domain.models.SubscriptionModel
import com.omarkarimli.cora.domain.models.UsageDataModel
import com.omarkarimli.cora.domain.models.UserModel
import com.omarkarimli.cora.domain.repository.FirestoreRepository
import com.omarkarimli.cora.ui.theme.Durations
import com.omarkarimli.cora.utils.Constants
import com.omarkarimli.cora.utils.Constants.MONTH_IN_MILLIS
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

    private val usersCollection = firestore.collection("users")
    private val subscriptionsCollection = firestore.collection("subscriptions")
    private val reportsCollection = firestore.collection("reportIssues")
    private val guidelinesCollection = firestore.collection("guidelines")
    private val categoriesCollection = firestore.collection("categories")
    private val journalsCollection = firestore.collection("journals")

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
        val maxRetries = Constants.MAX_RETRIES_GETTING_USER
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


    override suspend fun setSubscriptionModels() {
        val subscriptionModelsM = listOf(
            SubscriptionModel(
                title = "Basic",
                price = 0.0,
                subscriptionType = "monthly",
                maxUsageData = UsageDataModel(
                    webSearches = 10,
                    attaches = 10,
                    messageChars = 100,
                    webSearchResultCount = 1
                ),
                adsEnabled = true
            ),
            SubscriptionModel(
                title = "Plus",
                price = 19.99,
                subscriptionType = "monthly",
                maxUsageData = UsageDataModel(
                    webSearches = 30,
                    attaches = 30,
                    messageChars = 300,
                    webSearchResultCount = 2
                ),
                adsEnabled = false
            ),
            SubscriptionModel(
                title = "Pro",
                price = 39.99,
                subscriptionType = "monthly",
                maxUsageData = UsageDataModel(
                    webSearches = 100,
                    attaches = 100,
                    messageChars = 1000,
                    webSearchResultCount = 5
                ),
                adsEnabled = false
            )
        )
        val subscriptionModelsA = listOf(
            SubscriptionModel(
                title = "Plus",
                price = 229.99,
                subscriptionType = "annual",
                maxUsageData = UsageDataModel(
                    webSearches = 360,
                    attaches = 360,
                    messageChars = 3600,
                    webSearchResultCount = 2
                ),
                adsEnabled = false
            ),
            SubscriptionModel(
                title = "Pro",
                price = 459.99,
                subscriptionType = "annual",
                maxUsageData = UsageDataModel(
                    webSearches = 1200,
                    attaches = 1200,
                    messageChars = 12000,
                    webSearchResultCount = 5
                ),
                adsEnabled = false
            )
        )

        // Create a Map to store the monthly subscriptions
        val monthlyData = hashMapOf(
            "subscriptions" to subscriptionModelsM
        )

        // Create a Map to store the annual subscriptions
        val annualData = hashMapOf(
            "subscriptions" to subscriptionModelsA
        )

        try {
            subscriptionsCollection
                .document("monthly")
                .set(monthlyData)
                .await()

            subscriptionsCollection
                .document("annual")
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

    override suspend fun setCategories() {
        val list = listOf(
            CategoryModel(
                id = 0,
                title = "Casual",
                subtitle = "For everyday use",
                imageModels = listOf(
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1J1wOOJ-8Mb7qkbJf4IEyo93fiIxBhLDw/view?usp=sharing",
                        sourceUrl = "",
                        gender = "female"
                    ),
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1E38vPV5QIzI_-ChcZY4CXPHbLRSqSA2H/view?usp=sharing",
                        sourceUrl = "",
                        gender = "female"
                    ),
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1fodcqOnKz_XRZOzbCw4AMWQphXJK_scd/view?usp=sharing",
                        sourceUrl = "",
                        gender = "male"
                    ),
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1UpgGaVZ-j3KUJGavg0Afe6BN3QYpPaOY/view?usp=sharing",
                        sourceUrl = "",
                        gender = "male"
                    )
                )
            ),
            CategoryModel(
                id = 1,
                title = "Formal",
                subtitle = "For formal occasions",
                imageModels = listOf(
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1ONaiz2uzetXB_kVD7cr1CxMEn3My6j1T/view?usp=sharing",
                        sourceUrl = "",
                        gender = "female"
                    ),
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1e6gbnNrqi63_HqQ_MIflCiK5MfRaWaFF/view?usp=sharing",
                        sourceUrl = "",
                        gender = "female"
                    ),
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1LKedkPqfT7OWzcGORy4ebgHX7OpVBF4N/view?usp=sharing",
                        sourceUrl = "",
                        gender = "male"
                    ),
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1vkGiTOoSAqxY2uKO_iBvhnRmwOF3xTE8/view?usp=sharing",
                        sourceUrl = "",
                        gender = "male"
                    )
                )
            ),
            CategoryModel(
                id = 2,
                title = "Trendy",
                subtitle = "For trendy fashion",
                imageModels = listOf(
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1IKVasqP7n_dZN-SnsmzsQAEJX30GX5eL/view?usp=sharing",
                        sourceUrl = "",
                        gender = "female"
                    ),
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1ZtzO6BITOsDQsJ5u-FjH3B-4HbCLw8rL/view?usp=sharing",
                        sourceUrl = "",
                        gender = "female"
                    ),
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1847DxlIZduP1qbNK5-vOM24XpOwzUj6v/view?usp=sharing",
                        sourceUrl = "",
                        gender = "male"
                    ),
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1bMINmXmbFuU83pS6L6TLJTb6SRMgBDBv/view?usp=sharing",
                        sourceUrl = "",
                        gender = "male"
                    )
                )
            ),
            CategoryModel(
                id = 3,
                title = "Sporty",
                subtitle = "For sports enthusiasts",
                imageModels = listOf(
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/15ZjQGeBApPkdPv7sTgz7XlYBsL0UOxU7/view?usp=sharing",
                        sourceUrl = "",
                        gender = "female"
                    ),
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1wmo2eyxQan6HnjSzuGPuIRyn0Zw3xTUV/view?usp=sharing",
                        sourceUrl = "",
                        gender = "female"
                    ),
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1tgsG8MJbBkrTGWsajhNh7k-sZgb9kmLa/view?usp=sharing",
                        sourceUrl = "",
                        gender = "male"
                    ),
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/17gr8s8f37N0zNN3SSstjPUhBh8aQzIB7/view?usp=sharing",
                        sourceUrl = "",
                        gender = "male"
                    )
                )
            ),
            CategoryModel(
                id = 4,
                title = "Party",
                subtitle = "For party-goers",
                imageModels = listOf(
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1s60rRtOQruGqjNvC_xU004u-DVbZHT_w/view?usp=sharing",
                        sourceUrl = "",
                        gender = "female"
                    ),
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/105U-FlortOfGftSPnuhapfrXBKt5wT3D/view?usp=sharing",
                        sourceUrl = "",
                        gender = "female"
                    ),
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1nZj31NdbxzhQzrXRc-h2S0Y6h9N_kCZL/view?usp=sharing",
                        sourceUrl = "",
                        gender = "male"
                    ),
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1hthUlAM6Xb-vp1pEvCjvxVGRb5bnDp79/view?usp=sharing",
                        sourceUrl = "",
                        gender = "male"
                    )
                )
            ),
            CategoryModel(
                id = 5,
                title = "Streetwear",
                subtitle = "For street-wear enthusiasts",
                imageModels = listOf(
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/182JN3z1ZgUDxSZROJBLH7meMUUGq3wMJ/view?usp=sharing",
                        sourceUrl = "",
                        gender = "female"
                    ),
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1Dh2ZtOrj0NNH-OlJAA4oIJHejepXpGjL/view?usp=sharing",
                        sourceUrl = "",
                        gender = "female"
                    ),
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/12HIfPtqoDgXByaE-OZMnftusJZpY4g23/view?usp=sharing",
                        sourceUrl = "",
                        gender = "male"
                    ),
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/19qKvYVawnvg5ZCVEcV15AOmGYSwRJ0Ox/view?usp=sharing",
                        sourceUrl = "",
                        gender = "male"
                    )
                )
            ),
            CategoryModel(
                id = 6,
                title = "Minimalist",
                subtitle = "For minimalist fashion",
                imageModels = listOf(
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1N4T0w-KaYuyb_F1A-STCd2lPnTX7Y8kV/view?usp=sharing",
                        sourceUrl = "",
                        gender = "female"
                    ),
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1f5AQGwX2nj-NrVJNWlhZqk7gJdsOw__i/view?usp=sharing",
                        sourceUrl = "",
                        gender = "female"
                    ),
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1oP3Ab2iAFW1JTKMqaaxjH08wmrHUrt6S/view?usp=sharing",
                        sourceUrl = "",
                        gender = "male"
                    ),
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1ryt46ytJP9yzWgiQHCy2eKyVQMCzAb5t/view?usp=sharing",
                        sourceUrl = "",
                        gender = "male"
                    )
                )
            )
        )

        try {
            for (item in list) {
                categoriesCollection
                    .document(item.id.toString())
                    .set(item)
                    .await()
            }
            Log.d("FirestoreRepositoryImpl", "Successfully set categories.")
        } catch (e: Exception) {
            Log.e("FirestoreRepositoryImpl", "setCategories: ${e.message}")
        }
    }

    override suspend fun setJournals() {
        val list = listOf(
            JournalModel(
                id = 0,
                title = "Categories based on gender",
                description = "Categories on Home Page based on gender that you selected at User Setup Page. That's why you should select gender correctly to achieve your wish.",
                images = listOf(
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1-RTIAFBa7r7GCQ23i79MdAZ5qNzf3gA0/view?usp=drive_link"
                    ),
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1wqYNlWa6flhXpN2_Xsl2f3p2kMtJSYzh/view?usp=drive_link"
                    )
                ),
            ),
            JournalModel(
                id = 1,
                title = "Content accuracy",
                description = "The app generates and displays content using AI. Some details may not always be fully accurate based on your images or messages. Please keep this in mind — we’re continuously working to improve it.",
                images = listOf(
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/11TWh1u2fZTDHIrSgBRsWTVar3b51XRmG/view?usp=drive_link"
                    ),
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1jlRyzOHDhgu-FkFji_g4HHKw2fiQsjJP/view?usp=drive_link"
                    ),
                )
            ),
            JournalModel(
                id = 2,
                title = "Content accesibility",
                description = "Sometimes images can be seen like this image placeholder. Because some image sources can be inaccessible or while gathering data there can occur some issues. And also sometimes images can be loaded late. Please be patient.",
                images = listOf(
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1jI4R2JoOQYPfDoBHxfIz8kadmUWCz0zH/view?usp=drive_link"
                    )
                ),
            ),
            JournalModel(
                id = 3,
                title = "Voux Vision",
                description = "For giving accurate answers, you should place clear and bright images.",
                images = listOf(
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1HZ8hdgzdz7_fhDnJoHbi29jADSJnDWfQ/view?usp=drive_link"
                    ),
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1PY-XlQUhF8E4eckWefJNZTjdyayFumhS/view?usp=drive_link"
                    ),
                ),
            ),
            JournalModel(
                id = 4,
                title = "Workflows",
                description = "You can take a photo using the camera, choose one from your gallery, or paste an image link in Paste URL section.",
                images = listOf(
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1rzcrvrblBrueTnjTcGxkhPCd2eLjb8Gv/view?usp=drive_link"
                    )
                ),
            ),
            JournalModel(
                id = 5,
                title = "Chat",
                description = "Ask Voux for new outfit ideas or pick one you generated before. You can tell where each image comes from by the small icons (Device or Web Link) in the top-right corner.",
                images = listOf(
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1aUlZ0jtGMUKlq_o83-D05viT-9rlQnkW/view?usp=drive_link"
                    ),
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1I8heLtjlGQkmAin98WprMCG-Wi0Ygbin/view?usp=drive_link"
                    ),
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1aNAlgbyfET2CHJeaEZUQ0NbaJMjyAYLv/view?usp=drive_link"
                    ),
                ),
            ),
            JournalModel(
                id = 6,
                title = "Cross Page features",
                description = "You can open the chat from almost anywhere. By tapping the top-right “More” icon, you can also select images from the chat to generate a new Voux.",
                images = listOf(
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1uGHp9grsb1AU5liasw5nqQLR_KC4biWe/view?usp=drive_link"
                    ),
                    ImageModel(
                        imageUrl = "https://drive.google.com/file/d/1YwadChnYHSECv2YXzmQSL4NvGOoFu3VM/view?usp=drive_link"
                    )
                ),
            ),
        )

        try {
            for (item in list) {
                journalsCollection
                    .document(item.id.toString())
                    .set(item)
                    .await()
            }
            Log.d("FirestoreRepositoryImpl", "Successfully set journals.")
        } catch (e: Exception) {
            Log.e("FirestoreRepositoryImpl", "setJournals: ${e.message}")
        }
    }
}
