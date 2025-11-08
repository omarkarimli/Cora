package com.omarkarimli.cora.domain.use_case.sp

import com.omarkarimli.cora.domain.repository.SharedPreferenceRepository
import javax.inject.Inject

class SaveBooleanUseCase @Inject constructor(
    private val repository: SharedPreferenceRepository
) {
    suspend operator fun invoke(key: String, value: Boolean) = repository.saveBoolean(key, value)
}