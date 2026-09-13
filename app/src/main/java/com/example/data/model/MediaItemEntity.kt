package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MediaType {
    PICTURE,
    PDF,
    LINK
}

@Entity(tableName = "media_items")
data class MediaItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val type: MediaType,
    val contentUriOrUrl: String,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
