package com.omarkarimli.cora.domain.use_case.firestore

import com.omarkarimli.cora.domain.repository.FirestoreRepository
import javax.inject.Inject

class GetCreditConditionsUseCase @Inject constructor(
    private val repository: FirestoreRepository
) {
    operator fun invoke() = repository.creditConditions
}