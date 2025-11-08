package com.omarkarimli.cora.domain.use_case.permission

data class PermissionUseCases(
    val getCameraPermissionUseCase: GetCameraPermissionUseCase,
    val getStoragePermissionUseCase: GetStoragePermissionUseCase,
    val checkPermissionUseCase: CheckPermissionUseCase,
    val notifyPermissionChangedUseCase: NotifyPermissionChangedUseCase
)