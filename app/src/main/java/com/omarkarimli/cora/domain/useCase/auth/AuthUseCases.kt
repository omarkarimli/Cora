package com.omarkarimli.cora.domain.useCase.auth

import com.omarkarimli.cora.domain.useCase.firestore.GetUserUseCase
import com.omarkarimli.cora.domain.useCase.sp.SaveBooleanUseCase

data class AuthUseCases(
    val signInWithCredentialUseCase: SignInWithCredentialUseCase,
    val getUserUseCase: GetUserUseCase,
    val saveBooleanUseCase: SaveBooleanUseCase
)