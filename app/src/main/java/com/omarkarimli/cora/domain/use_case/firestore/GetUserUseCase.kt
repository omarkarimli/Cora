package com.omarkarimli.cora.domain.use_case.firestore

import com.omarkarimli.cora.domain.repository.FirestoreRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val repository: FirestoreRepository
) {
    suspend operator fun invoke() = repository.getUser()
}