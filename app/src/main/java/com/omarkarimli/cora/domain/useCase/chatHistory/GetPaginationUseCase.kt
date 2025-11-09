package com.omarkarimli.cora.domain.useCase.chatHistory

import com.omarkarimli.cora.domain.repository.ChatHistoryRepository
import javax.inject.Inject

class GetPaginationUseCase @Inject constructor(
    private val repository: ChatHistoryRepository
) {
    operator fun invoke(searchQuery: String) = repository.getPagination(searchQuery)
}