package com.omarkarimli.cora.domain.repository

import com.omarkarimli.cora.domain.models.MessageModel

interface ChatRepository {
    suspend fun sendMessage(messageModel: MessageModel): MessageModel
}