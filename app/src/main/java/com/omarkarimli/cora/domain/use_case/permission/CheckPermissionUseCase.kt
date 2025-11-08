package com.omarkarimli.cora.domain.use_case.permission

import com.omarkarimli.cora.domain.repository.PermissionRepository
import javax.inject.Inject

class CheckPermissionUseCase @Inject constructor(
    private val repository: PermissionRepository
) {
    operator fun invoke(permission: String) = repository.checkPermission(permission)
}