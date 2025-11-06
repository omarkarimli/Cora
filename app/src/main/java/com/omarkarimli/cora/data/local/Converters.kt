package com.omarkarimli.cora.data.local

import androidx.room.TypeConverter
import com.omarkarimli.cora.domain.models.ImageModel
import com.omarkarimli.cora.domain.models.MessageModel
import com.omarkarimli.cora.domain.models.UserModel
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class Converters {
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
    fun fromMessageModels(value: List<MessageModel>): String {
        val itemJson = Json.encodeToString(value)
        val encodedItemJson = URLEncoder.encode(itemJson, StandardCharsets.UTF_8.toString())

        return encodedItemJson
    }

    @TypeConverter
    fun toMessageModels(value: String): List<MessageModel> {
        val decodedItemJson = URLDecoder.decode(value, StandardCharsets.UTF_8.toString())
        val item = Json.decodeFromString<List<MessageModel>>(decodedItemJson)

        return item
    }
}