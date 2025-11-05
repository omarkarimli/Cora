package com.omarkarimli.cora.domain.models.serper


import com.google.gson.annotations.SerializedName

data class RelatedSearch(
    @SerializedName("query")
    val query: String = ""
)