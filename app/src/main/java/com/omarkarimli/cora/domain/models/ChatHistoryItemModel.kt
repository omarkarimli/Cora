package com.omarkarimli.cora.domain.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "chat_history"
)
@Serializable
data class ChatHistoryItemModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "title")
    val title: String = "",
    @ColumnInfo(name = "messages")
    val messages: List<MessageModel> = emptyList(),
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),
)