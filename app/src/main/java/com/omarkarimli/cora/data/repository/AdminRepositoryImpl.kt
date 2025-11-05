package com.omarkarimli.cora.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.omarkarimli.cora.domain.models.CategoryModel
import com.omarkarimli.cora.domain.models.GuidelineModel
import com.omarkarimli.cora.domain.models.ImageModel
import com.omarkarimli.cora.domain.models.JournalModel
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
    private val categoriesCollection = firestore.collection(FirebaseConstants.CATEGORIES)
    private val journalsCollection = firestore.collection(FirebaseConstants.JOURNALS)

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
