package com.omarkarimli.cora.domain.useCase.chat

import com.omarkarimli.cora.domain.useCase.chatHistory.ChatHistoryUseCases
import com.omarkarimli.cora.domain.useCase.firestore.FirestoreUseCases
import com.omarkarimli.cora.domain.useCase.sp.SharedPreferenceUseCases
import com.omarkarimli.cora.domain.useCase.translate.TranslateUseCase

data class ChatUseCases(
    val sendMessageUseCase: SendMessageUseCase,
    val firestoreUseCases: FirestoreUseCases,
    val sharedPreferenceUseCases: SharedPreferenceUseCases,
    val chatHistoryUseCases: ChatHistoryUseCases,
    val translateUseCase: TranslateUseCase
)