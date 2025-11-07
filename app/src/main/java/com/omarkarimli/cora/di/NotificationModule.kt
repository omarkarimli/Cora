package com.omarkarimli.cora.di

import com.omarkarimli.cora.data.notification.NotificationServiceImpl
import com.omarkarimli.cora.domain.notification.NotificationService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {

    @Binds
    @Singleton
    abstract fun bindNotificationService(notificationServiceImpl: NotificationServiceImpl): NotificationService
}