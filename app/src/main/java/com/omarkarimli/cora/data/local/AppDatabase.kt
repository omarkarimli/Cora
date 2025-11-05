package com.omarkarimli.cora.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.omarkarimli.cora.domain.models.ChatHistoryItemModel
import com.omarkarimli.cora.domain.models.FavCategoryDetail
import com.omarkarimli.cora.domain.models.FavOutfit
import com.omarkarimli.cora.domain.models.HistoryCategoryDetail
import com.omarkarimli.cora.domain.models.HistoryOutfit

@Database(
    entities = [
        FavOutfit::class,
        HistoryOutfit::class,
        FavCategoryDetail::class,
        HistoryCategoryDetail::class,
        ChatHistoryItemModel::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favOutfitDao(): FavOutfitDao
    abstract fun historyOutfitDao(): HistoryOutfitDao
    abstract fun favCategoryDetailDao(): FavCategoryDetailDao
    abstract fun historyCategoryDetailDao(): HistoryCategoryDetailDao
    abstract fun chatHistoryDao(): ChatHistoryDao
}