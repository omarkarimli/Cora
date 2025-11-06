package com.omarkarimli.cora.di

import com.omarkarimli.cora.data.repository.AdminRepositoryImpl
import com.omarkarimli.cora.data.repository.AiRepositoryImpl
import com.omarkarimli.cora.data.repository.AuthRepositoryImpl
import com.omarkarimli.cora.data.repository.ChatHistoryRepoImpl
import com.omarkarimli.cora.data.repository.PermissionRepositoryImpl
import com.omarkarimli.cora.data.repository.ChatRepositoryImpl
import com.omarkarimli.cora.data.repository.DownloadRepositoryImpl
import com.omarkarimli.cora.data.repository.FirestoreRepositoryImpl
import com.omarkarimli.cora.data.repository.LangRepositoryImpl
import com.omarkarimli.cora.data.repository.NotificationRepositoryImpl
import com.omarkarimli.cora.data.repository.SerperRepositoryImpl
import com.omarkarimli.cora.data.repository.SharedPreferenceRepositoryImpl
import com.omarkarimli.cora.data.repository.ThemeRepositoryImpl
import com.omarkarimli.cora.data.repository.TranslateRepositoryImpl
import com.omarkarimli.cora.domain.repository.AdminRepository
import com.omarkarimli.cora.domain.repository.AiRepository
import com.omarkarimli.cora.domain.repository.AuthRepository
import com.omarkarimli.cora.domain.repository.ChatHistoryRepo
import com.omarkarimli.cora.domain.repository.PermissionRepository
import com.omarkarimli.cora.domain.repository.ChatRepository
import com.omarkarimli.cora.domain.repository.DownloadRepository
import com.omarkarimli.cora.domain.repository.FirestoreRepository
import com.omarkarimli.cora.domain.repository.LangRepository
import com.omarkarimli.cora.domain.repository.NotificationRepository
import com.omarkarimli.cora.domain.repository.SerperRepository
import com.omarkarimli.cora.domain.repository.SharedPreferenceRepository
import com.omarkarimli.cora.domain.repository.ThemeRepository
import com.omarkarimli.cora.domain.repository.TranslateRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAdminRepository(
        adminRepositoryImpl: AdminRepositoryImpl
    ): AdminRepository

    @Binds
    @Singleton
    abstract fun bindPermissionRepository(
        permissionRepositoryImpl: PermissionRepositoryImpl
    ): PermissionRepository

    @Binds
    @Singleton
    abstract fun bindSharedPreferenceRepository(
        sharedPreferenceRepositoryImpl: SharedPreferenceRepositoryImpl
    ): SharedPreferenceRepository

    @Binds
    @Singleton
    abstract fun bindThemeRepository(
        themeRepositoryImpl: ThemeRepositoryImpl
    ): ThemeRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindFirestoreRepository(
        firestoreRepositoryImpl: FirestoreRepositoryImpl
    ): FirestoreRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        chatRepositoryImpl: ChatRepositoryImpl
    ): ChatRepository

    @Binds
    @Singleton
    abstract fun bindChatHistoryRepository(
        chatHistoryRepoImpl: ChatHistoryRepoImpl
    ): ChatHistoryRepo

    @Binds
    @Singleton
    abstract fun bindDownloadRepository(
        downloadRepositoryImpl: DownloadRepositoryImpl
    ): DownloadRepository

    @Binds
    @Singleton
    abstract fun bindSerperRepository(
        serperRepositoryImpl: SerperRepositoryImpl
    ): SerperRepository

    @Binds
    @Singleton
    abstract fun bindAiRepository(
        aiRepositoryImpl: AiRepositoryImpl
    ): AiRepository

    @Binds
    @Singleton
    abstract fun bindLangRepository(
        langRepositoryImpl: LangRepositoryImpl
    ): LangRepository

    @Binds
    @Singleton
    abstract fun bindTranslateRepository(
        translateRepositoryImpl: TranslateRepositoryImpl
    ): TranslateRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository
}