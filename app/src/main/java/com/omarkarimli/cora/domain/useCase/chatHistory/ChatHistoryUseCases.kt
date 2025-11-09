package com.omarkarimli.cora.domain.useCase.chatHistory

data class ChatHistoryUseCases(
    val getInstanceUseCase: GetInstanceUseCase,
    val insertInstanceUseCase: InsertInstanceUseCase,
    val getPaginationUseCase: GetPaginationUseCase
)