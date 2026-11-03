package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.dao.DiaryEntryDao
import com.example.data.local.entity.EncryptedDiaryEntryEntity

/**
 * Main Room Database for Lykke local persistence.
 * Stores encrypted emotional journal entries on the device.
 */
@Database(
    entities = [EncryptedDiaryEntryEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DiaryConverters::class)
abstract class LykkeDatabase : RoomDatabase() {

    abstract fun diaryEntryDao(): DiaryEntryDao

    companion object {
        private const val DATABASE_NAME = "lykke_journal_encrypted.db"

        @Volatile
        private var INSTANCE: LykkeDatabase? = null

        fun getInstance(context: Context): LykkeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LykkeDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
