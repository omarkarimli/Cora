package com.omarkarimli.cora.data.local

import androidx.room.TypeConverter
import com.omarkarimli.cora.domain.models.CategoryDetailModel
import com.omarkarimli.cora.domain.models.CategoryModel
import com.omarkarimli.cora.domain.models.ChatHistoryItemModel
import com.omarkarimli.cora.domain.models.ClothModel
import com.omarkarimli.cora.domain.models.ImageModel
import com.omarkarimli.cora.domain.models.ItemAnalysisModel
import com.omarkarimli.cora.domain.models.MessageModel
import com.omarkarimli.cora.domain.models.UserModel
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class Converters {

    @TypeConverter
    fun fromMessages(value: List<MessageModel>): String {
        val itemJson = Json.encodeToString(value)
        val encodedItemJson = URLEncoder.encode(itemJson, StandardCharsets.UTF_8.toString())

        return encodedItemJson
    }

    @TypeConverter
    fun toMessages(value: String): List<MessageModel> {
        val decodedItemJson = URLDecoder.decode(value, StandardCharsets.UTF_8.toString())
        val item = Json.decodeFromString<List<MessageModel>>(decodedItemJson)

        return item
    }

    @TypeConverter
    fun fromChatHistoryItemModel(value: ChatHistoryItemModel): String {
        val itemJson = Json.encodeToString(value)
        val encodedItemJson = URLEncoder.encode(itemJson, StandardCharsets.UTF_8.toString())

        return encodedItemJson
    }

    @TypeConverter
    fun toChatHistoryItemModel(value: String): ChatHistoryItemModel {
        val decodedItemJson = URLDecoder.decode(value, StandardCharsets.UTF_8.toString())
        val item = Json.decodeFromString<ChatHistoryItemModel>(decodedItemJson)

        return item
    }

    @TypeConverter
    fun fromUserModel(value: UserModel): String {
        val itemJson = Json.encodeToString(value)
        val encodedItemJson = URLEncoder.encode(itemJson, StandardCharsets.UTF_8.toString())

        return encodedItemJson
    }

    @TypeConverter
    fun toUserModel(value: String): UserModel {
        val decodedItemJson = URLDecoder.decode(value, StandardCharsets.UTF_8.toString())
        val item = Json.decodeFromString<UserModel>(decodedItemJson)

        return item
    }

    @TypeConverter
    fun fromCategoryModel(value: CategoryModel): String {
        val itemJson = Json.encodeToString(value)
        val encodedItemJson = URLEncoder.encode(itemJson, StandardCharsets.UTF_8.toString())

        return encodedItemJson
    }

    @TypeConverter
    fun toCategoryModel(value: String): CategoryModel {
        val decodedItemJson = URLDecoder.decode(value, StandardCharsets.UTF_8.toString())
        val item = Json.decodeFromString<CategoryModel>(decodedItemJson)

        return item
    }

    @TypeConverter
    fun fromImageModels(value: List<ImageModel>): String {
        val itemJson = Json.encodeToString(value)
        val encodedItemJson = URLEncoder.encode(itemJson, StandardCharsets.UTF_8.toString())

        return encodedItemJson
    }

    @TypeConverter
    fun toImageModels(value: String): List<ImageModel> {
        val decodedItemJson = URLDecoder.decode(value, StandardCharsets.UTF_8.toString())
        val item = Json.decodeFromString<List<ImageModel>>(decodedItemJson)

        return item
    }

    @TypeConverter
    fun fromClothModels(value: List<ClothModel>): String {
        val itemJson = Json.encodeToString(value)
        val encodedItemJson = URLEncoder.encode(itemJson, StandardCharsets.UTF_8.toString())

        return encodedItemJson
    }

    @TypeConverter
    fun toClothModels(value: String): List<ClothModel> {
        val decodedItemJson = URLDecoder.decode(value, StandardCharsets.UTF_8.toString())
        val item = Json.decodeFromString<List<ClothModel>>(decodedItemJson)

        return item
    }

    @TypeConverter
    fun fromItemAnalysisModel(value: ItemAnalysisModel): String {
        val itemJson = Json.encodeToString(value)
        val encodedItemJson = URLEncoder.encode(itemJson, StandardCharsets.UTF_8.toString())

        return encodedItemJson
    }

    @TypeConverter
    fun toItemAnalysisModel(value: String): ItemAnalysisModel {
        val decodedItemJson = URLDecoder.decode(value, StandardCharsets.UTF_8.toString())
        val item = Json.decodeFromString<ItemAnalysisModel>(decodedItemJson)

        return item
    }

    @TypeConverter
    fun fromCategoryResultModel(value: CategoryDetailModel): String {
        val itemJson = Json.encodeToString(value)
        val encodedItemJson = URLEncoder.encode(itemJson, StandardCharsets.UTF_8.toString())

        return encodedItemJson
    }

    @TypeConverter
    fun toCategoryResultModel(value: String): CategoryDetailModel {
        val decodedItemJson = URLDecoder.decode(value, StandardCharsets.UTF_8.toString())
        val item = Json.decodeFromString<CategoryDetailModel>(decodedItemJson)

        return item
    }
}