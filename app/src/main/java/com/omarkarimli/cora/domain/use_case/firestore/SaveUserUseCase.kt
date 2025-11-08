package com.omarkarimli.cora.domain.use_case.firestore

import com.omarkarimli.cora.domain.models.UserModel
import com.omarkarimli.cora.domain.repository.FirestoreRepository
import javax.inject.Inject

class SaveUserUseCase @Inject constructor(
    private val repository: FirestoreRepository
) {
    suspend operator fun invoke(userModel: UserModel) = repository.saveUser(userModel)
}