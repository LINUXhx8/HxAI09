package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.MediaType

class Converters {
    @TypeConverter
    fun fromMediaType(value: MediaType): String {
        return value.name
    }

    @TypeConverter
    fun toMediaType(value: String): MediaType {
        return try {
            MediaType.valueOf(value)
        } catch (e: Exception) {
            MediaType.LINK
        }
    }
}
