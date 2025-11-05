package com.omarkarimli.cora.domain.models.serper


import com.google.gson.annotations.SerializedName

data class PeopleAlsoAsk(
    @SerializedName("link")
    val link: String?,
    @SerializedName("question")
    val question: String?,
    @SerializedName("snippet")
    val snippet: String?,
    @SerializedName("title")
    val title: String?
)