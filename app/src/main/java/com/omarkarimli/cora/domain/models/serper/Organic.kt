package com.omarkarimli.cora.domain.models.serper


import com.google.gson.annotations.SerializedName

data class Organic(
    @SerializedName("date")
    val date: String?,
    @SerializedName("link")
    val link: String?,
    @SerializedName("position")
    val position: Int?,
    @SerializedName("sitelinks")
    val sitelinks: List<Sitelink?>?,
    @SerializedName("snippet")
    val snippet: String?,
    @SerializedName("title")
    val title: String?
)