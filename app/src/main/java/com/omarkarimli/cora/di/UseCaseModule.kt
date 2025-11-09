package com.omarkarimli.cora.di

import com.omarkarimli.cora.domain.repository.AuthRepository
import com.omarkarimli.cora.domain.repository.ChatRepository
import com.omarkarimli.cora.domain.repository.FirestoreRepository
import com.omarkarimli.cora.domain.repository.SharedPreferenceRepository
import com.omarkarimli.cora.domain.repository.TranslateRepository
import com.omarkarimli.cora.domain.use_case.auth.AuthUseCases
import com.omarkarimli.cora.domain.use_case.auth.SignInWithCredentialUseCase
import com.omarkarimli.cora.domain.use_case.chat.ChatUseCases
import com.omarkarimli.cora.domain.use_case.chat.SendMessageUseCase
import com.omarkarimli.cora.domain.use_case.chat_history.ChatHistoryUseCases
import com.omarkarimli.cora.domain.use_case.chat_history.GetInstanceUseCase
import com.omarkarimli.cora.domain.use_case.chat_history.GetPaginationUseCase
import com.omarkarimli.cora.domain.use_case.chat_history.InsertInstanceUseCase
import com.omarkarimli.cora.domain.use_case.firestore.AddReportIssueUseCase
import com.omarkarimli.cora.domain.use_case.firestore.FirestoreUseCases
import com.omarkarimli.cora.domain.use_case.firestore.GetCreditConditionsUseCase
import com.omarkarimli.cora.domain.use_case.firestore.GetFreeSubscriptionsUseCase
import com.omarkarimli.cora.domain.use_case.firestore.GetUserUseCase
import com.omarkarimli.cora.domain.use_case.firestore.SaveUserUseCase
import com.omarkarimli.cora.domain.use_case.sp.GetBooleanUseCase
import com.omarkarimli.cora.domain.use_case.sp.SaveBooleanUseCase
import com.omarkarimli.cora.domain.use_case.sp.SharedPreferenceUseCases
import com.omarkarimli.cora.domain.use_case.translate.TranslateUseCase
import com.omarkarimli.cora.domain.use_case.user_setup.UserSetupUseCases
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
        chatRepository: ChatRepository,
        translateRepository: TranslateRepository,
        getBooleanUseCase: GetBooleanUseCase,
        getInstanceUseCase: GetInstanceUseCase,
        insertInstanceUseCase: InsertInstanceUseCase,
        getPaginationUseCase: GetPaginationUseCase,
        getUserUseCase: GetUserUseCase,
        addReportIssueUseCase: AddReportIssueUseCase,
        getCreditConditionsUseCase: GetCreditConditionsUseCase
    ): ChatUseCases {
        return ChatUseCases(
            sendMessageUseCase = SendMessageUseCase(chatRepository),
            firestoreUseCases = FirestoreUseCases(
                getUserUseCase = getUserUseCase,
                addReportIssueUseCase = addReportIssueUseCase,
                getCreditConditionsUseCase = getCreditConditionsUseCase
            ),
            sharedPreferenceUseCases = SharedPreferenceUseCases(getBooleanUseCase),
            chatHistoryUseCases = ChatHistoryUseCases(
                getInstanceUseCase = getInstanceUseCase,
                insertInstanceUseCase = insertInstanceUseCase,
                getPaginationUseCase = getPaginationUseCase
            ),
            translateUseCase = TranslateUseCase(translateRepository)
        )
    }
}