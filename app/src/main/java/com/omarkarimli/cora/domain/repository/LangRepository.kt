package com.omarkarimli.cora.domain.repository

interface LangRepository {

    fun getDefaultLanguageCode(): String

    fun getLanguageCode(): String

    suspend fun changeLanguage(languageCode: String)
}