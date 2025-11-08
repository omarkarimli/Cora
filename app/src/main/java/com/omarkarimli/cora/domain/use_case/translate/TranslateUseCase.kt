package com.omarkarimli.cora.domain.use_case.translate

import com.omarkarimli.cora.domain.repository.TranslateRepository
import javax.inject.Inject

class TranslateUseCase @Inject constructor(
    private val repository: TranslateRepository
) {
    suspend operator fun invoke(sourceText: String) = repository.translate(sourceText)
}