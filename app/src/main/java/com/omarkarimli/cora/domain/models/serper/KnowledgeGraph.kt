package com.omarkarimli.cora.domain.models.serper


import com.google.gson.annotations.SerializedName

data class KnowledgeGraph(
    @SerializedName("description")
    val description: String = "",
    @SerializedName("descriptionLink")
    val descriptionLink: String = "",
    @SerializedName("descriptionSource")
    val descriptionSource: String = "",
    @SerializedName("imageUrl")
    val imageUrl: String = "",
    @SerializedName("title")
    val title: String = ""
)