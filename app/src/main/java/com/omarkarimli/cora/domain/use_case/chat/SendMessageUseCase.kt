package com.omarkarimli.cora.domain.use_case.chat

import com.omarkarimli.cora.domain.models.MessageModel
import com.omarkarimli.cora.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(messageModel: MessageModel) = repository.sendMessage(messageModel)
}