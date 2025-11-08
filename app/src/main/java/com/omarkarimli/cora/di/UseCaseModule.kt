package com.omarkarimli.cora.di

import com.omarkarimli.cora.domain.repository.AuthRepository
import com.omarkarimli.cora.domain.repository.FirestoreRepository
import com.omarkarimli.cora.domain.repository.SharedPreferenceRepository
import com.omarkarimli.cora.domain.use_case.auth.AuthUseCases
import com.omarkarimli.cora.domain.use_case.auth.SignInWithCredentialUseCase
import com.omarkarimli.cora.domain.use_case.firestore.GetFreeSubscriptionsUseCase
import com.omarkarimli.cora.domain.use_case.firestore.GetUserUseCase
import com.omarkarimli.cora.domain.use_case.firestore.SaveUserUseCase
import com.omarkarimli.cora.domain.use_case.sp.SaveBooleanUseCase
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
    fun provideAuthUseCases(authRepository: AuthRepository, firestoreRepository: FirestoreRepository, sharedPreferenceRepository: SharedPreferenceRepository): AuthUseCases {
        return AuthUseCases(
            signInWithCredentialUseCase = SignInWithCredentialUseCase(authRepository),
            getUserUseCase = GetUserUseCase(firestoreRepository),
            saveBooleanUseCase = SaveBooleanUseCase(sharedPreferenceRepository)
        )
    }

    @Provides
    @Singleton
    fun provideUserSetupUseCases(firestoreRepository: FirestoreRepository, sharedPreferenceRepository: SharedPreferenceRepository): UserSetupUseCases {
        return UserSetupUseCases(
            getFreeSubscriptionsUseCase = GetFreeSubscriptionsUseCase(firestoreRepository),
            saveUserUseCase = SaveUserUseCase(firestoreRepository),
            saveBooleanUseCase = SaveBooleanUseCase(sharedPreferenceRepository)
        )
    }
}