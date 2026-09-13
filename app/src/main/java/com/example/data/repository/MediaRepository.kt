package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.data.local.MediaDao
import com.example.data.model.MediaItemEntity
import com.example.data.model.MediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class MediaRepository(
    private val mediaDao: MediaDao,
    private val context: Context
) {
    val allMedia: Flow<List<MediaItemEntity>> = mediaDao.getAllMedia()

    suspend fun saveUriToFile(uri: Uri, extension: String, subfolder: String): String = withContext(Dispatchers.IO) {
        val dir = File(context.filesDir, subfolder)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val filename = "hxai_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.$extension"
        val destinationFile = File(dir, filename)

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(destinationFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        destinationFile.absolutePath
    }

    suspend fun insertMedia(
        title: String,
        type: MediaType,
        contentUriOrUrl: String,
        note: String = ""
    ) = withContext(Dispatchers.IO) {
        val item = MediaItemEntity(
            title = title,
            type = type,
            contentUriOrUrl = contentUriOrUrl,
            note = note,
            timestamp = System.currentTimeMillis()
        )
        mediaDao.insertMedia(item)
    }

    suspend fun deleteMedia(item: MediaItemEntity) = withContext(Dispatchers.IO) {
        // If it's a local file created in internal storage, clean it up
        try {
            if (item.type != MediaType.LINK) {
                val file = File(item.contentUriOrUrl)
                if (file.exists() && file.absolutePath.startsWith(context.filesDir.absolutePath)) {
                    file.delete()
                }
            }
        } catch (_: Exception) {
        }
        mediaDao.deleteMedia(item)
    }

    suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        if (mediaDao.getCount() == 0) {
            // Seed sample AI & Robotics resources highlighting Java and Python foundations
            mediaDao.insertMedia(
                MediaItemEntity(
                    title = "HxAI Neural Robotics Architecture (Python & Java)",
                    type = MediaType.LINK,
                    contentUriOrUrl = "https://github.com",
                    note = "High-level robotics middleware linking Python PyTorch vision algorithms with Java ROS controllers.",
                    timestamp = System.currentTimeMillis() - 60000
                )
            )
            mediaDao.insertMedia(
                MediaItemEntity(
                    title = "Autonomous Navigation & Kinematics Blueprint",
                    type = MediaType.LINK,
                    contentUriOrUrl = "https://en.wikipedia.org/wiki/Robotics",
                    note = "Comprehensive documentation on mobile robot kinematics and sensor fusion matrices.",
                    timestamp = System.currentTimeMillis() - 120000
                )
            )
            mediaDao.insertMedia(
                MediaItemEntity(
                    title = "Python Deep Learning for Robotics Vision",
                    type = MediaType.LINK,
                    contentUriOrUrl = "https://pytorch.org",
                    note = "Neural object detection and real-time robotic arm manipulation using Python CNN models.",
                    timestamp = System.currentTimeMillis() - 180000
                )
            )
        }
    }
}
