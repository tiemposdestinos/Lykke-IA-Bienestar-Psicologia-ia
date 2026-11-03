package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.DiaryEntry

/**
 * Room entity representing an encrypted diary entry stored locally on device.
 */
@Entity(tableName = "encrypted_diary_entries")
data class EncryptedDiaryEntryEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val encryptedContent: String,
    val plainContent: String = "",
    val tags: List<String> = emptyList(),
    val moodEmoji: String = "🌱",
    val authorAlias: String = "Usuario Confidencial",
    val timestamp: Long = System.currentTimeMillis(),
    val isSyncedWithCloud: Boolean = false,
    val userId: String? = null
)

/**
 * Mapper extension functions between Room entity and domain model.
 */
fun DiaryEntry.toEntity(userId: String? = null, isSynced: Boolean = false): EncryptedDiaryEntryEntity {
    return EncryptedDiaryEntryEntity(
        id = this.id,
        title = this.title,
        encryptedContent = this.encryptedContent,
        plainContent = this.plainContent,
        tags = this.tags,
        moodEmoji = this.moodEmoji,
        authorAlias = this.authorAlias,
        timestamp = this.timestamp,
        isSyncedWithCloud = isSynced,
        userId = userId
    )
}

fun EncryptedDiaryEntryEntity.toDomainModel(): DiaryEntry {
    return DiaryEntry(
        id = this.id,
        title = this.title,
        plainContent = this.plainContent,
        encryptedContent = this.encryptedContent,
        tags = this.tags,
        moodEmoji = this.moodEmoji,
        authorAlias = this.authorAlias,
        timestamp = this.timestamp
    )
}
