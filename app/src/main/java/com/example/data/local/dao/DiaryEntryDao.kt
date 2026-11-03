package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.EncryptedDiaryEntryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for local encrypted diary entries in Room.
 */
@Dao
interface DiaryEntryDao {

    @Query("SELECT * FROM encrypted_diary_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<EncryptedDiaryEntryEntity>>

    @Query("SELECT * FROM encrypted_diary_entries WHERE userId = :userId ORDER BY timestamp DESC")
    fun getEntriesByUserId(userId: String): Flow<List<EncryptedDiaryEntryEntity>>

    @Query("SELECT * FROM encrypted_diary_entries WHERE id = :id LIMIT 1")
    fun getEntryById(id: String): Flow<EncryptedDiaryEntryEntity?>

    @Query("SELECT * FROM encrypted_diary_entries WHERE id = :id LIMIT 1")
    suspend fun getEntryByIdOnce(id: String): EncryptedDiaryEntryEntity?

    @Query("SELECT * FROM encrypted_diary_entries WHERE title LIKE '%' || :query || '%' OR plainContent LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchEntries(query: String): Flow<List<EncryptedDiaryEntryEntity>>

    @Query("SELECT COUNT(*) FROM encrypted_diary_entries")
    fun getEntryCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: EncryptedDiaryEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntries(entries: List<EncryptedDiaryEntryEntity>)

    @Update
    suspend fun updateEntry(entry: EncryptedDiaryEntryEntity)

    @Delete
    suspend fun deleteEntry(entry: EncryptedDiaryEntryEntity)

    @Query("DELETE FROM encrypted_diary_entries WHERE id = :id")
    suspend fun deleteEntryById(id: String)

    @Query("DELETE FROM encrypted_diary_entries")
    suspend fun deleteAllEntries()
}
