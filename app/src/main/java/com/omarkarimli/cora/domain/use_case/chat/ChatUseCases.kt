package com.omarkarimli.cora.domain.use_case.chat

import com.omarkarimli.cora.domain.use_case.chat_history.ChatHistoryUseCases
import com.omarkarimli.cora.domain.use_case.firestore.FirestoreUseCases
import com.omarkarimli.cora.domain.use_case.sp.SharedPreferenceUseCases
import com.omarkarimli.cora.domain.use_case.translate.TranslateUseCase

data class ChatUseCases(
    val sendMessageUseCase: SendMessageUseCase,
    val firestoreUseCases: FirestoreUseCases,
    val sharedPreferenceUseCases: SharedPreferenceUseCases,
    val chatHistoryUseCases: ChatHistoryUseCases,
    val translateUseCase: TranslateUseCase
)