package com.omarkarimli.cora.domain.models.serper


import com.google.gson.annotations.SerializedName

data class Organic(
    @SerializedName("date")
    val date: String = "",
    @SerializedName("link")
    val link: String = "",
    @SerializedName("position")
    val position: Int = 0,
    @SerializedName("sitelinks")
    val sitelinks: List<Sitelink> = emptyList(),
    @SerializedName("snippet")
    val snippet: String = "",
    @SerializedName("title")
    val title: String = ""
)