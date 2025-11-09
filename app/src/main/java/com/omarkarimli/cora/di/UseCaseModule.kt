package com.omarkarimli.cora.di

import com.omarkarimli.cora.domain.repository.AuthRepository
import com.omarkarimli.cora.domain.repository.ChatHistoryRepository
import com.omarkarimli.cora.domain.repository.ChatRepository
import com.omarkarimli.cora.domain.repository.FirestoreRepository
import com.omarkarimli.cora.domain.repository.SharedPreferenceRepository
import com.omarkarimli.cora.domain.repository.TranslateRepository
import com.omarkarimli.cora.domain.useCase.auth.AuthUseCases
import com.omarkarimli.cora.domain.useCase.auth.SignInWithCredentialUseCase
import com.omarkarimli.cora.domain.useCase.chat.ChatUseCases
import com.omarkarimli.cora.domain.useCase.chat.SendMessageUseCase
import com.omarkarimli.cora.domain.useCase.chatHistory.ChatHistoryUseCases
import com.omarkarimli.cora.domain.useCase.chatHistory.GetInstanceUseCase
import com.omarkarimli.cora.domain.useCase.chatHistory.GetPaginationUseCase
import com.omarkarimli.cora.domain.useCase.chatHistory.InsertInstanceUseCase
import com.omarkarimli.cora.domain.useCase.firestore.AddReportIssueUseCase
import com.omarkarimli.cora.domain.useCase.firestore.FirestoreUseCases
import com.omarkarimli.cora.domain.useCase.firestore.GetCreditConditionsUseCase
import com.omarkarimli.cora.domain.useCase.firestore.GetFreeSubscriptionsUseCase
import com.omarkarimli.cora.domain.useCase.firestore.GetUserUseCase
import com.omarkarimli.cora.domain.useCase.firestore.SaveUserUseCase
import com.omarkarimli.cora.domain.useCase.sp.GetBooleanUseCase
import com.omarkarimli.cora.domain.useCase.sp.SaveBooleanUseCase
import com.omarkarimli.cora.domain.useCase.sp.SharedPreferenceUseCases
import com.omarkarimli.cora.domain.useCase.translate.TranslateUseCase
import com.omarkarimli.cora.domain.useCase.userSetup.UserSetupUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideAuthUseCases(
        authRepository: AuthRepository,
        firestoreRepository: FirestoreRepository,
        sharedPreferenceRepository: SharedPreferenceRepository
    ): AuthUseCases {
        return AuthUseCases(
            signInWithCredentialUseCase = SignInWithCredentialUseCase(authRepository),
            getUserUseCase = GetUserUseCase(firestoreRepository),
            saveBooleanUseCase = SaveBooleanUseCase(sharedPreferenceRepository)
        )
    }

    @Provides
    @Singleton
    fun provideUserSetupUseCases(
        firestoreRepository: FirestoreRepository,
        sharedPreferenceRepository: SharedPreferenceRepository
    ): UserSetupUseCases {
        return UserSetupUseCases(
            getFreeSubscriptionsUseCase = GetFreeSubscriptionsUseCase(firestoreRepository),
            saveUserUseCase = SaveUserUseCase(firestoreRepository),
            saveBooleanUseCase = SaveBooleanUseCase(sharedPreferenceRepository)
        )
    }

    @Provides
    @Singleton
    fun provideChatUseCases(
        firestoreRepository: FirestoreRepository,
        sharedPreferenceRepository: SharedPreferenceRepository,
        chatRepository: ChatRepository,
        chatHistoryRepository: ChatHistoryRepository,
        translateRepository: TranslateRepository
    ): ChatUseCases {
        return ChatUseCases(
            sendMessageUseCase = SendMessageUseCase(chatRepository),
            firestoreUseCases = FirestoreUseCases(
                getUserUseCase = GetUserUseCase(firestoreRepository),
                addReportIssueUseCase = AddReportIssueUseCase(firestoreRepository),
                getCreditConditionsUseCase = GetCreditConditionsUseCase(firestoreRepository)
            ),
            sharedPreferenceUseCases = SharedPreferenceUseCases(GetBooleanUseCase(sharedPreferenceRepository)),
            chatHistoryUseCases = ChatHistoryUseCases(
                getInstanceUseCase = GetInstanceUseCase(chatHistoryRepository),
                insertInstanceUseCase = InsertInstanceUseCase(chatHistoryRepository),
                getPaginationUseCase = GetPaginationUseCase(chatHistoryRepository)
            ),
            translateUseCase = TranslateUseCase(translateRepository)
        )
    }
}