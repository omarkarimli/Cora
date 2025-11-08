package com.omarkarimli.cora.domain.use_case.chat_history

import com.omarkarimli.cora.domain.repository.ChatHistoryRepo
import javax.inject.Inject

class GetPaginationUseCase @Inject constructor(
    private val repository: ChatHistoryRepo
) {
    operator fun invoke(searchQuery: String) = repository.getPagination(searchQuery)
}