package com.omarkarimli.cora.domain.use_case.chat_history

import com.omarkarimli.cora.domain.repository.ChatHistoryRepository
import javax.inject.Inject

class GetPaginationUseCase @Inject constructor(
    private val repository: ChatHistoryRepository
) {
    operator fun invoke(searchQuery: String) = repository.getPagination(searchQuery)
}