package com.omarkarimli.cora.domain.repository

import android.net.Uri
import kotlinx.coroutines.flow.StateFlow

interface PermissionRepository {
    fun getCameraPermission(): String
    fun getLocationPermissions(): List<String>
    fun getStoragePermission(): String
    fun checkPermission(permission: String): Boolean
    fun notifyPermissionChanged(permission: String)

    val cameraPermissionState: StateFlow<Boolean>
    val storagePermissionState: StateFlow<Boolean>
    val locationPermissionState: StateFlow<Boolean>

    fun getNewImageUri(): Uri?
}