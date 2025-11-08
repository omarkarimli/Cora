package com.omarkarimli.cora.domain.use_case.auth

import com.omarkarimli.cora.domain.use_case.firestore.GetUserUseCase
import com.omarkarimli.cora.domain.use_case.sp.SaveBooleanUseCase

data class AuthUseCases(
    val signInWithCredentialUseCase: SignInWithCredentialUseCase,
    val getUserUseCase: GetUserUseCase,
    val saveBooleanUseCase: SaveBooleanUseCase
)