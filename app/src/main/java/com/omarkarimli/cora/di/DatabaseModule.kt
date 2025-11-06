package com.omarkarimli.cora.di

import android.content.Context
import androidx.room.Room
import com.omarkarimli.cora.data.local.AppDatabase
import com.omarkarimli.cora.data.local.ChatHistoryDao
import com.omarkarimli.cora.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Constants.LOCAL_DB
        ).build()
    }

    @Provides
    @Singleton
    fun provideChatHistoryDao(appDatabase: AppDatabase): ChatHistoryDao {
        return appDatabase.chatHistoryDao()
    }
}