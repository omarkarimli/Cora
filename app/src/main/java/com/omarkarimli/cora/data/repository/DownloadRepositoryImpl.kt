package com.omarkarimli.cora.data.repository

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toUri
import com.omarkarimli.cora.domain.repository.DownloadRepository
import com.omarkarimli.cora.domain.repository.PermissionRepository
import com.omarkarimli.cora.domain.repository.SharedPreferenceRepository
import com.omarkarimli.cora.utils.SpConstant.SAVING_PATH_KEY
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val permissionRepository: PermissionRepository,
    private val sharedPreferenceRepository: SharedPreferenceRepository
) : DownloadRepository {

    val appName: String = context.applicationInfo.name

    override suspend fun downloadImage(imageUrl: String): Uri? {
        val savingPath = sharedPreferenceRepository.getString(SAVING_PATH_KEY, "photos")
        return if (savingPath == "photos") saveImageToPhotos(imageUrl)
        else saveImageToDownloads(imageUrl)
    }

    override suspend fun downloadBitmap(bitmap: Bitmap): Uri? {
        val savingPath = sharedPreferenceRepository.getString(SAVING_PATH_KEY, "photos")
        return if (savingPath == "photos") saveBitmapToPhotos(bitmap)
        else saveBitmapToDownloads(bitmap)
    }

    override suspend fun saveImageToPhotos(imageUrl: String): Uri? {
        return try {
            withContext(Dispatchers.IO) {
                val imageUri = permissionRepository.getNewImageUri()

                imageUri?.let { uri ->
                    context.contentResolver.openOutputStream(uri).use { outputStream ->
                        outputStream ?: throw Exception("Failed to open output stream for MediaStore URI: $uri (saveImageToPhotos)")

                        val url = URL(imageUrl)
                        val connection = url.openConnection()
                        // Optional: Set timeouts
                        // connection.connectTimeout = 15000
                        // connection.readTimeout = 15000
                        connection.connect()

                        connection.getInputStream().use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                    uri
                }
            }
        } catch (e: Exception) {
            Log.e("DownloadRepositoryImpl", "saveImageToPhotos Error: ${e.message}", e)
            null
        }
    }

    override suspend fun saveImageToDownloads(imageUrl: String): Uri? {
        return try {
            withContext(Dispatchers.IO) {
                val filename = System.currentTimeMillis().toString() + ".jpg"
                val mimeType = "image/jpeg"
                var finalImageUri: Uri? = null

                val url = URL(imageUrl)
                val connection = url.openConnection()
                // Optional: Set timeouts
                // connection.connectTimeout = 15000
                // connection.readTimeout = 15000
                connection.connect()

                connection.getInputStream().use { inputStream -> // Ensures inputStream is closed
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val contentValues = ContentValues().apply {
                            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/" + appName)
                        }
                        val newUri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                        newUri?.let { uri ->
                            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                                inputStream.copyTo(outputStream)
                                finalImageUri = uri
                            } ?: throw Exception("Failed to open output stream for URI: $uri (saveImageToDownloads Q+)")
                        } ?: throw Exception("MediaStore.Downloads.EXTERNAL_CONTENT_URI insert returned null (saveImageToDownloads Q+).")
                    } else {
                        // Legacy approach: Requires WRITE_EXTERNAL_STORAGE
                        if (permissionRepository.storagePermissionState.value) {
                            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            val saveDir = File(downloadsDir, appName)
                            if (!saveDir.exists() && !saveDir.mkdirs()) {
                                throw Exception("Failed to create directory: ${saveDir.absolutePath} (saveImageToDownloads legacy)")
                            }

                            val imageFile = File(saveDir, filename)
                            FileOutputStream(imageFile).use { outputStream ->
                                inputStream.copyTo(outputStream)
                            }

                            Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { intent ->
                                intent.data = imageFile.toUri() // Consistent with saveBitmapToDownloads
                                context.sendBroadcast(intent)
                            }
                            finalImageUri = imageFile.toUri() // Consistent with saveBitmapToDownloads
                        } else {
                            Log.w("DownloadRepositoryImpl", "saveImageToDownloads: Storage permission denied on older API. Cannot save image.")
                            return@withContext null
                        }
                    }
                }
                finalImageUri
            }
        } catch (e: Exception) {
            Log.e("DownloadRepositoryImpl", "saveImageToDownloads General Error: ${e.message}", e)
            null
        }
    }

    override suspend fun saveBitmapToPhotos(bitmap: Bitmap): Uri? {
        return try {
            withContext(Dispatchers.IO) {
                val imageUri = permissionRepository.getNewImageUri()

                imageUri?.let { uri ->
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    } ?: throw Exception("Failed to open output stream for URI: $uri (saveBitmapToPhotos)")
                    uri
                }
            }
        } catch (e: Exception) {
            Log.e("DownloadRepositoryImpl", "saveBitmapToPhotos Error: ${e.message}", e)
            null
        }
    }

    override suspend fun saveBitmapToDownloads(bitmap: Bitmap): Uri? {
        return try {
            withContext(Dispatchers.IO) {
                val filename = "${appName}_${System.currentTimeMillis()}.png"
                val mimeType = "image/png"
                var finalImageUri: Uri? = null

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/" + appName)
                    }
                    val newUri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    newUri?.let { uri ->
                        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                            finalImageUri = uri
                        } ?: throw Exception("Failed to open output stream for URI: $uri (saveBitmapToDownloads Q+)")
                    } ?: throw Exception("MediaStore.Downloads.EXTERNAL_CONTENT_URI insert returned null (saveBitmapToDownloads Q+).")
                } else {
                    if (permissionRepository.storagePermissionState.value) {
                        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        val saveDir = File(downloadsDir, appName)
                        if (!saveDir.exists() && !saveDir.mkdirs()) {
                            throw Exception("Failed to create directory: ${saveDir.absolutePath} (saveBitmapToDownloads legacy)")
                        }
                        val imageFile = File(saveDir, filename)
                        FileOutputStream(imageFile).use { outputStream ->
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                        }
                        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { intent ->
                            intent.data = imageFile.toUri()
                            context.sendBroadcast(intent)
                        }
                        finalImageUri = imageFile.toUri()
                    } else {
                        Log.w("DownloadRepositoryImpl", "saveBitmapToDownloads: Storage permission denied on older API. Cannot save bitmap.")
                        return@withContext null
                    }
                }
                finalImageUri
            }
        } catch (e: Exception) {
            Log.e("DownloadRepositoryImpl", "saveBitmapToDownloads Error: ${e.message}", e)
            null
        }
    }
}
