package com.omarkarimli.cora.domain.use_case.chat_history

import com.omarkarimli.cora.domain.repository.ChatHistoryRepo
import javax.inject.Inject

class GetInstanceUseCase @Inject constructor(
    private val repository: ChatHistoryRepo
) {
    suspend operator fun invoke(id: Int) = repository.getInstance(id)
}