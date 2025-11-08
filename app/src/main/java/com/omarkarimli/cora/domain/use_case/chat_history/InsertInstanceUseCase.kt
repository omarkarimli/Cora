package com.omarkarimli.cora.domain.use_case.chat_history

import com.omarkarimli.cora.domain.models.ChatHistoryItemModel
import com.omarkarimli.cora.domain.repository.ChatHistoryRepo
import javax.inject.Inject

class InsertInstanceUseCase @Inject constructor(
    private val repository: ChatHistoryRepo
) {
    suspend operator fun invoke(item: ChatHistoryItemModel) = repository.insertInstance(item)
}