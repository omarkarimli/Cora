package com.omarkarimli.cora.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.ai.type.GenerateContentResponse
import com.omarkarimli.cora.domain.models.CategoryModel
import com.omarkarimli.cora.domain.models.CategoryDetailModel
import com.omarkarimli.cora.domain.models.ImageModel
import com.omarkarimli.cora.domain.models.ItemAnalysisModel
import com.omarkarimli.cora.domain.models.MessageModel

interface AiRepository {

    suspend fun convertImageModelsToBitmaps(imageModels: List<ImageModel>): List<Bitmap>
    suspend fun generateMessage(messageModel: MessageModel): MessageModel
    suspend fun getImageFromResponse(response: GenerateContentResponse): List<ImageModel>

    suspend fun searchWebpage(url: String): Uri
    suspend fun analyzeImage(imagePath: String): ItemAnalysisModel
    suspend fun getCategoryDetail(categoryModel: CategoryModel): CategoryDetailModel
    suspend fun processCategory(categoryModel: CategoryModel, gender: String): CategoryDetailModel
    suspend fun processImage(imagePath: String): ItemAnalysisModel
    suspend fun searchImageModels(q: String): List<ImageModel>
    suspend fun generateItemAnalysisModel(bitmaps: List<Bitmap>): ItemAnalysisModel
    suspend fun generateCategoryDetail(bitmaps: List<Bitmap>): CategoryDetailModel
}