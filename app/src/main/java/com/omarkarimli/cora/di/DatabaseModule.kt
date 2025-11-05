package com.omarkarimli.cora.di

import android.content.Context
import androidx.room.Room
import com.omarkarimli.cora.data.local.AppDatabase
import com.omarkarimli.cora.data.local.ChatHistoryDao
import com.omarkarimli.cora.data.local.FavOutfitDao
import com.omarkarimli.cora.data.local.FavCategoryDetailDao
import com.omarkarimli.cora.data.local.HistoryCategoryDetailDao
import com.omarkarimli.cora.data.local.HistoryOutfitDao
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
    fun provideFavOutfitDao(appDatabase: AppDatabase): FavOutfitDao {
        return appDatabase.favOutfitDao()
    }

    @Provides
    @Singleton
    fun provideHistoryOutfitDao(appDatabase: AppDatabase): HistoryOutfitDao {
        return appDatabase.historyOutfitDao()
    }

    @Provides
    @Singleton
    fun provideFavCategoryDetailDao(appDatabase: AppDatabase): FavCategoryDetailDao {
        return appDatabase.favCategoryDetailDao()
    }

    @Provides
    @Singleton
    fun provideHistoryCategoryDetailDao(appDatabase: AppDatabase): HistoryCategoryDetailDao {
        return appDatabase.historyCategoryDetailDao()
    }

    @Provides
    @Singleton
    fun provideChatHistoryDao(appDatabase: AppDatabase): ChatHistoryDao {
        return appDatabase.chatHistoryDao()
    }
}