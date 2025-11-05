package com.omarkarimli.cora.domain.repository

import android.graphics.Bitmap
import android.net.Uri

interface DownloadRepository {
    suspend fun downloadImage(imageUrl: String): Uri?
    suspend fun downloadBitmap(bitmap: Bitmap): Uri?
    suspend fun saveImageToPhotos(imageUrl: String): Uri?
    suspend fun saveImageToDownloads(imageUrl: String): Uri?
    suspend fun saveBitmapToPhotos(bitmap: Bitmap): Uri?
    suspend fun saveBitmapToDownloads(bitmap: Bitmap): Uri?
}