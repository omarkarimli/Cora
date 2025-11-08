package com.omarkarimli.cora.domain.use_case.sp

import com.omarkarimli.cora.domain.repository.SharedPreferenceRepository
import javax.inject.Inject

class GetBooleanUseCase @Inject constructor(
    private val repository: SharedPreferenceRepository
) {
    suspend operator fun invoke(key: String, defaultValue: Boolean) = repository.getBoolean(key, defaultValue)
}