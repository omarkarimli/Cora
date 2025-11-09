package com.omarkarimli.cora.domain.useCase.auth

import com.google.firebase.auth.AuthCredential
import com.omarkarimli.cora.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithCredentialUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(credential: AuthCredential) = repository.signInWithCredential(credential)
}