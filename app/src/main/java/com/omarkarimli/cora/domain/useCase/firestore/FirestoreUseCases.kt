package com.omarkarimli.cora.domain.useCase.firestore

data class FirestoreUseCases(
    val getUserUseCase: GetUserUseCase,
    val addReportIssueUseCase: AddReportIssueUseCase,
    val getCreditConditionsUseCase: GetCreditConditionsUseCase
)