package com.omarkarimli.cora.data.repository

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import com.omarkarimli.cora.domain.repository.PermissionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PermissionRepository {

    override fun getStoragePermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    override fun getCameraPermission(): String {
        return Manifest.permission.CAMERA
    }

    override fun getLocationPermissions(): List<String> {
        return listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private val _cameraPermissionState = MutableStateFlow(checkPermission(Manifest.permission.CAMERA))
    override val cameraPermissionState: StateFlow<Boolean> = _cameraPermissionState

    private val _storagePermissionState = MutableStateFlow(checkPermission(getStoragePermission()))
    override val storagePermissionState: StateFlow<Boolean> = _storagePermissionState

    private val _locationPermissionState = MutableStateFlow(checkAllPermissions(getLocationPermissions()))
    override val locationPermissionState: StateFlow<Boolean> = _locationPermissionState

    override fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkAllPermissions(permissions: List<String>): Boolean {
        return permissions.all { checkPermission(it) }
    }

    override fun notifyPermissionChanged(permission: String) {
        when (permission) {
            Manifest.permission.CAMERA -> _cameraPermissionState.value = checkPermission(Manifest.permission.CAMERA)
            getStoragePermission() -> _storagePermissionState.value = checkPermission(getStoragePermission())
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION -> _locationPermissionState.value = checkAllPermissions(getLocationPermissions())
        }
    }

    override fun getNewImageUri(): Uri? {
        // Determine the storage location based on the Android version
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val contentValues = ContentValues().apply {
            val filename = "voux_image_${System.currentTimeMillis()}.jpg"
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg") // Change to jpeg if your images are JPEGs

            // For Android 10 (API 29) and above, use RELATIVE_PATH
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // This is the key change: specify the subdirectory "Voux"
                put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/Voux")
            } else {
                // For older versions, this is the deprecated way
                val directory = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "Voux"
                )
                if (!directory.exists()) {
                    directory.mkdirs()
                }
                put(
                    MediaStore.Images.Media.DATA,
                    "${directory.path}/$filename"
                )
            }
        }

        return context.contentResolver.insert(collection, contentValues)
    }
}