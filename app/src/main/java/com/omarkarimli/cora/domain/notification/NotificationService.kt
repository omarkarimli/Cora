package com.omarkarimli.cora.domain.notification

interface NotificationService {
    suspend fun scheduleDailyNotifications()
}