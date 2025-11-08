package com.omarkarimli.cora.domain.use_case.chat_history

data class ChatHistoryUseCases(
    val getInstanceUseCase: GetInstanceUseCase,
    val insertInstanceUseCase: InsertInstanceUseCase,
    val getPaginationUseCase: GetPaginationUseCase
)