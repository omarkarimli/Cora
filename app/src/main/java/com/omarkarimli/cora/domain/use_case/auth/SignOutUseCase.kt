package com.omarkarimli.cora.domain.use_case.auth

import com.omarkarimli.cora.domain.repository.AuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() = repository.signOut()
}
