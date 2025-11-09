package com.omarkarimli.cora.domain.use_case.chat_history

import com.omarkarimli.cora.domain.repository.ChatHistoryRepository
import javax.inject.Inject

class GetInstanceUseCase @Inject constructor(
    private val repository: ChatHistoryRepository
) {
    suspend operator fun invoke(id: Int) = repository.getInstance(id)
}