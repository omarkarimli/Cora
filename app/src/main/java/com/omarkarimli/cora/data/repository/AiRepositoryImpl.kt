package com.omarkarimli.cora.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.firebase.ai.FirebaseAI
import com.google.firebase.ai.type.GenerateContentResponse
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.asImageOrNull
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import com.omarkarimli.cora.data.local.Converters
import com.omarkarimli.cora.domain.models.CategoryDetailModel
import com.omarkarimli.cora.domain.models.CategoryModel
import com.omarkarimli.cora.domain.models.ImageModel
import com.omarkarimli.cora.domain.models.ItemAnalysisModel
import com.omarkarimli.cora.domain.models.MessageModel
import com.omarkarimli.cora.domain.repository.AiRepository
import com.omarkarimli.cora.domain.repository.DownloadRepository
import com.omarkarimli.cora.domain.repository.FavCategoryDetailRepo
import com.omarkarimli.cora.domain.repository.FirestoreRepository
import com.omarkarimli.cora.domain.repository.HistoryOutfitRepo
import com.omarkarimli.cora.domain.repository.SerperRepository
import com.omarkarimli.cora.utils.Schemas
import com.omarkarimli.cora.utils.isWebUrl
import com.omarkarimli.cora.utils.toImageModels
import com.omarkarimli.cora.utils.toTitle
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAI: FirebaseAI,
    private val serperRepository: SerperRepository,
    private val firestoreRepository: FirestoreRepository,
    private val historyOutfitRepo: HistoryOutfitRepo,
    private val favCategoryDetailRepo: FavCategoryDetailRepo,
    private val downloadRepository: DownloadRepository
) : AiRepository {

    override suspend fun convertImageModelsToBitmaps(imageModels: List<ImageModel>): List<Bitmap> {
        val bitmaps: List<Bitmap> = if (imageModels.isNotEmpty()) {
            withContext(Dispatchers.IO) {
                val imageLoader = ImageLoader(context) // Consider injecting this
                imageModels.mapNotNull { imageModel ->
                    try {
                        val request = ImageRequest.Builder(context)
                            // Coil handles both web URLs and local URIs (e.g., content://)
                            .data(imageModel.imageUrl)
                            .allowHardware(false) // Requesting software bitmap
                            .build()

                        val result = imageLoader.execute(request)

                        if (result is SuccessResult) {
                            // Get the bitmap from the drawable
                            (result.drawable as? BitmapDrawable)?.bitmap
                        } else {
                            Log.e("AiRepositoryImpl", "Failed to load image with Coil: ${imageModel.imageUrl}")
                            null
                        }
                    } catch (e: Exception) {
                        Log.e("AiRepositoryImpl", "Error processing imageModel: ${imageModel.imageUrl}", e)
                        null
                    }
                }
            }
        } else {
            emptyList()
        }
        return bitmaps
    }

    override suspend fun generateMessage(messageModel: MessageModel): MessageModel {
        try {
            val bitmaps = convertImageModelsToBitmaps(messageModel.images)
            val generativeModel = if (messageModel.imageGeneration) {
                firebaseAI.generativeModel(
                    modelName = Schemas.AI_MODEL_2,
                    generationConfig = generationConfig {
                        responseModalities = listOf(ResponseModality.TEXT, ResponseModality.IMAGE)
                    }
                )
            } else {
                firebaseAI.generativeModel(modelName = Schemas.AI_MODEL_1)
            }

            val additionalText = messageModel.images.fold("") { acc, imageModel ->
                acc + if (imageModel.imageUrl.isWebUrl()) {
                    imageModel.imageUrl + "\n"
                } else ""
            }
            val promptText = additionalText + messageModel.text
            val prompt = content {
                if (bitmaps.isNotEmpty()) {
                    bitmaps.forEach {
                        image(it)
                    }
                }
                text(promptText)
            }
            val response = generativeModel.generateContent(prompt)
            val imageModels = getImageFromResponse(response)

            return MessageModel(
                text = response.text ?: "Response is empty.",
                images = imageModels,
                isFromMe = false
            )
        } catch (e: Exception) {
            Log.e("AiRepositoryImpl", "generateContent error: ${e.message}", e)
            throw Exception("Error generating content.", e)
        }
    }

    override suspend fun getImageFromResponse(response: GenerateContentResponse): List<ImageModel> {
        val imageModels = mutableListOf<ImageModel>()

        response.candidates.forEach { candidate ->
            candidate.content.parts.forEach { part ->
                val generatedImageAsBitmap: Bitmap? = part.asImageOrNull()
                generatedImageAsBitmap?.let {
                    val imageUrl = downloadRepository.downloadBitmap(generatedImageAsBitmap)
                    val imageModel = ImageModel(
                        imageUrl = imageUrl.toString()
                    )
                    imageModels.add(imageModel)
                }
            }
        }

        return imageModels
    }

    override suspend fun searchWebpage(url: String): Uri {
        return try {
            val imageUrlString = serperRepository.searchWebpage(url).metadata["og:image"]
            if (imageUrlString.isNullOrEmpty()) {
                throw IllegalStateException("Image not found.")
            }

            Log.d("AiRepositoryImpl", "imageUrlString: $imageUrlString")
            val localUri = downloadRepository.downloadImage(imageUrlString)
            if (localUri != null) Log.d("AiRepositoryImpl", "Failed to download image.")
            url.toUri()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun analyzeImage(imagePath: String): ItemAnalysisModel {
        return try {
            val historyInstance = historyOutfitRepo.getInstance(imagePath)

            historyInstance?.data ?: run {
                // Update
                val userModel = firestoreRepository.getUser() ?: throw IllegalStateException("Profile not found")
                val result = processImage(imagePath)

                val newUsageData = userModel.usageData.copy(
                    webSearches = userModel.usageData.webSearches + 1
                )
                firestoreRepository.updateUsageData(newUsageData)

                result
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun processImage(imagePath: String): ItemAnalysisModel {
        try {
            val userModel = firestoreRepository.getUser() ?: throw IllegalStateException("Profile not found.")
            val numResults = userModel.usageData.webSearchResultCount

            val bitmaps = convertImageModelsToBitmaps(listOf(ImageModel(imageUrl = imagePath)))

            // Generate
            var result = generateItemAnalysisModel(bitmaps)

            // Search
            result = result.copy(
                parts = result.parts.map { clothModel ->
                    val gender = if (result.gender.trim().isNotEmpty()) result.gender else ""
                    val q = gender + clothModel.toTitle()
                    val imageModels = searchImageModels(q)
                    clothModel.copy(
                        imageModels = imageModels.take(numResults)
                    )
                }
            )

            return result.copy(imagePath = imagePath)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun generateItemAnalysisModel(bitmaps: List<Bitmap>): ItemAnalysisModel {
        Log.d("AiRepositoryImpl", "Beginning of Result Generation")

        var result = ItemAnalysisModel()
        if (bitmaps.isNotEmpty()) {
            val itemAnalysisModelGenerativeModel = firebaseAI.generativeModel(
                modelName = Schemas.AI_MODEL_1,
                generationConfig = generationConfig {
                    responseMimeType = Schemas.JSON_MIME_TYPE
                    responseSchema = Schemas.itemAnalysisModelSchema
                }
            )
            val prompt = content {
                bitmaps.forEach {
                    image(it)
                }
                text("Give me ItemAnalysisModel from this image.")
            }
            val response = itemAnalysisModelGenerativeModel.generateContent(prompt)
            response.text?.let {
                result = Converters().toItemAnalysisModel(it)
            } ?: throw Exception("Something went wrong.")
        } else {
            throw Exception("No bitmaps found.")
        }

        Log.d("AiRepositoryImpl", "End of Result Generation")

        return result
    }

    override suspend fun generateCategoryDetail(bitmaps: List<Bitmap>): CategoryDetailModel {
        Log.d("AiRepositoryImpl", "Beginning of Category Detail Generation")

        var result = CategoryDetailModel()
        if (bitmaps.isNotEmpty()) {
            val categoryResultGenerativeModel = firebaseAI.generativeModel(
                modelName = Schemas.AI_MODEL_1
            )
            val prompt = content {
                bitmaps.forEach {
                    image(it)
                }
                text("Give me a short description of ${result.category.title} outfits.")
            }
            val response = categoryResultGenerativeModel.generateContent(prompt)
            response.text?.let {
                result = CategoryDetailModel(description = it)
            } ?: throw Exception("Something went wrong.")
        } else {
            throw Exception("No bitmaps found.")
        }

        Log.d("AiRepositoryImpl", "End of Category Detail Generation")

        return result
    }

    override suspend fun searchImageModels(q: String): List<ImageModel> {
        Log.d("AiRepositoryImpl", "Beginning of Searching Image Models")

        var result = serperRepository.searchImage(query = q).toImageModels()
        result = result.map {
            val localUri = downloadRepository.downloadImage(it.imageUrl)
            if (localUri != null) Log.d("AiRepositoryImpl", "Failed to download image.")
            it
        }

        Log.d("AiRepositoryImpl", "End of Searching Image Models")

        return result
    }

    override suspend fun getCategoryDetail(categoryModel: CategoryModel): CategoryDetailModel {
        val historyInstance = favCategoryDetailRepo.getInstance(categoryModel)

        return historyInstance?.data ?: run {
            // Update
            val userModel = firestoreRepository.getUser() ?: throw IllegalStateException("Profile not found.")
            val gender = userModel.personalInfo.gender

            val result = processCategory(categoryModel, gender)

            val newUsageData = userModel.usageData.copy(
                webSearches = userModel.usageData.webSearches + 1
            )
            firestoreRepository.updateUsageData(newUsageData)

            return result
        }
    }

    override suspend fun processCategory(categoryModel: CategoryModel, gender: String): CategoryDetailModel {
        try {
            val bitmaps = convertImageModelsToBitmaps(categoryModel.imageModels)

            // Generate
            var result = generateCategoryDetail(bitmaps)

            // Search
            val contentImages = searchImageModels("Find me $gender ${categoryModel.title} outfits")

            result = result.copy(
                category = categoryModel,
                contentImages = contentImages
            )
            return result
        } catch (e: Exception) {
            throw e
        }
    }
}