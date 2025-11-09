package com.omarkarimli.cora.domain.useCase.userSetup

import com.omarkarimli.cora.domain.useCase.firestore.GetFreeSubscriptionsUseCase
import com.omarkarimli.cora.domain.useCase.firestore.SaveUserUseCase
import com.omarkarimli.cora.domain.useCase.sp.SaveBooleanUseCase

data class UserSetupUseCases(
    val getFreeSubscriptionsUseCase: GetFreeSubscriptionsUseCase,
    val saveUserUseCase: SaveUserUseCase,
    val saveBooleanUseCase: SaveBooleanUseCase
)