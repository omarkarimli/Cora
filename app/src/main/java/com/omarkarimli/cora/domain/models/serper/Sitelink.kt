package com.omarkarimli.cora.domain.models.serper


import com.google.gson.annotations.SerializedName

data class Sitelink(
    @SerializedName("link")
    val link: String?,
    @SerializedName("title")
    val title: String?
)