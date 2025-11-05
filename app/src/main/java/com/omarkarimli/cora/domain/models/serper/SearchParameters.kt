package com.omarkarimli.cora.domain.models.serper


import com.google.gson.annotations.SerializedName

data class SearchParameters(
    @SerializedName("engine")
    val engine: String = "",
    @SerializedName("q")
    val q: String = "",
    @SerializedName("type")
    val type: String = ""
)