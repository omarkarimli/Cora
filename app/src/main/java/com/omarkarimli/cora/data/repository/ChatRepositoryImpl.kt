package com.omarkarimli.cora.data.repository

import android.util.Log
import com.omarkarimli.cora.domain.models.MessageModel
import com.omarkarimli.cora.domain.repository.AiRepository
import com.omarkarimli.cora.domain.repository.ChatRepository
import com.omarkarimli.cora.domain.repository.FirestoreRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    val firestoreRepository: FirestoreRepository,
    val aiRepository: AiRepository
) : ChatRepository {

    override suspend fun sendMessage(messageModel: MessageModel): MessageModel {
        try {
            val userModel = firestoreRepository.getUser() ?: throw IllegalStateException("User not authenticated.")
            val result = aiRepository.generateMessage(messageModel)

            val newUsageData = userModel.usageData.copy(
                attaches = userModel.usageData.attaches + messageModel.images.size,
                messageChars = userModel.usageData.messageChars + result.text.length
            )
            firestoreRepository.updateUsageData(newUsageData)

            return result
        } catch (e: Exception) {
            Log.e("ChatRepository", "Error sending message: ${e.message}")
            return MessageModel(
                text = "An error occurred while sending the message.",
                isFromMe = false
            )
        }
    }
}