package com.omarkarimli.cora.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.ai.type.GenerateContentResponse
import com.omarkarimli.cora.domain.models.ImageModel
import com.omarkarimli.cora.domain.models.MessageModel

interface AiRepository {

    suspend fun convertImageModelsToBitmaps(imageModels: List<ImageModel>): List<Bitmap>
    suspend fun generateMessage(messageModel: MessageModel): MessageModel
    suspend fun getImageFromResponse(response: GenerateContentResponse): List<ImageModel>

    suspend fun searchWebpage(url: String): Uri
    suspend fun searchImageModels(q: String): List<ImageModel>
}