package com.example.data.local

import androidx.room.TypeConverter

/**
 * Room TypeConverters for complex types like List<String> for diary tags.
 */
class DiaryConverters {

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return value?.joinToString("|||") ?: ""
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        return if (value.isNullOrBlank()) {
            emptyList()
        } else {
            value.split("|||").map { it.trim() }.filter { it.isNotEmpty() }
        }
    }
}
