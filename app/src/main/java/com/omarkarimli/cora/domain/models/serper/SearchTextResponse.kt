package com.omarkarimli.cora.domain.models.serper


import com.google.gson.annotations.SerializedName

data class SearchTextResponse(
    @SerializedName("credits")
    val credits: Int = 0,
    @SerializedName("knowledgeGraph")
    val knowledgeGraph: KnowledgeGraph = KnowledgeGraph(),
    @SerializedName("organic")
    val organic: List<Organic> = emptyList(),
    @SerializedName("peopleAlsoAsk")
    val peopleAlsoAsk: List<PeopleAlsoAsk> = emptyList(),
    @SerializedName("relatedSearches")
    val relatedSearches: List<RelatedSearch> = emptyList(),
    @SerializedName("searchParameters")
    val searchParameters: SearchParameters = SearchParameters(),
)