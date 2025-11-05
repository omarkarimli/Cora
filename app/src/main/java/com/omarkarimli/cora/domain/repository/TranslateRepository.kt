package com.omarkarimli.cora.domain.repository

import com.omarkarimli.cora.domain.models.TranslateModel
import kotlinx.coroutines.flow.StateFlow

interface TranslateRepository {
    val translations: StateFlow<List<TranslateModel>>

    suspend fun downloadModel()
    suspend fun translate(sourceText: String): String
    fun close()
}