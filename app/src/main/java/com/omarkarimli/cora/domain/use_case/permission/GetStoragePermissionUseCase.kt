package com.omarkarimli.cora.domain.use_case.permission

import com.omarkarimli.cora.domain.repository.PermissionRepository
import javax.inject.Inject

class GetStoragePermissionUseCase @Inject constructor(
    private val repository: PermissionRepository
) {
    operator fun invoke() = repository.getStoragePermission()
}