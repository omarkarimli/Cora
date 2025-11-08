package com.omarkarimli.cora.domain.use_case.user_setup

import com.omarkarimli.cora.domain.use_case.firestore.GetFreeSubscriptionsUseCase
import com.omarkarimli.cora.domain.use_case.firestore.SaveUserUseCase
import com.omarkarimli.cora.domain.use_case.sp.SaveBooleanUseCase

data class UserSetupUseCases(
    val getFreeSubscriptionsUseCase: GetFreeSubscriptionsUseCase,
    val saveUserUseCase: SaveUserUseCase,
    val saveBooleanUseCase: SaveBooleanUseCase
)