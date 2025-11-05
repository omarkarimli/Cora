package com.omarkarimli.cora.domain.repository

interface NotificationRepository {
    fun scheduleDailyNotifications()
    fun scheduleNotification(hour: Int)
}