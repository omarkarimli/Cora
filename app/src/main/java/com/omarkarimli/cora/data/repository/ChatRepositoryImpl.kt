package com.omarkarimli.cora.data.repository

import android.util.Log
import com.omarkarimli.cora.domain.models.MessageModel
import com.omarkarimli.cora.domain.repository.AiRepository
import com.omarkarimli.cora.domain.repository.ChatRepository
import com.omarkarimli.cora.domain.repository.FirestoreRepository
import com.omarkarimli.cora.domain.repository.SerperRepository
import com.omarkarimli.cora.utils.toMessageModel
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    val firestoreRepository: FirestoreRepository,
    val aiRepository: AiRepository,
    val serperRepository: SerperRepository
) : ChatRepository {

    override suspend fun sendMessage(messageModel: MessageModel): MessageModel {
        try {
            val userModel = firestoreRepository.getUser() ?: throw IllegalStateException("User not authenticated.")
            val result = if (messageModel.webSearch) {
                serperRepository.searchText(messageModel.text).toMessageModel()
            } else {
                aiRepository.generateMessage(messageModel)
            }

            Log.d(
                "ChatRepository",
                if (messageModel.webSearch) "Web search" else "Generated AI"
            )
            Log.d("ChatRepository", "Message: ${messageModel.text}")

            val newUsageData = userModel.usageData.copy(
                attaches = userModel.usageData.attaches + messageModel.images.size
                        + if (messageModel.webSearch) 1 else 0
                        + if (messageModel.imageGeneration) 1 else 0,
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