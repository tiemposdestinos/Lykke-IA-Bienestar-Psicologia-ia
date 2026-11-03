package com.example.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.dao.DiaryEntryDao
import com.example.data.local.entity.EncryptedDiaryEntryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DiaryRoomDatabaseTest {

    private lateinit var db: LykkeDatabase
    private lateinit var dao: DiaryEntryDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, LykkeDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.diaryEntryDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndRetrieveEncryptedDiaryEntry() = runBlocking {
        val entry = EncryptedDiaryEntryEntity(
            id = "test-uuid-001",
            title = "Superando la ansiedad matutina",
            encryptedContent = "ENC_SECURE::U2VjcmV0Q29udGVudA==",
            plainContent = "Hoy sentí un nudo en el pecho al despertar.",
            tags = listOf("ansiedad", "respiración", "calma"),
            moodEmoji = "🌱",
            authorAlias = "ViajeroZen",
            timestamp = 1700000000000L,
            isSyncedWithCloud = false,
            userId = "user_abc_123"
        )

        dao.insertEntry(entry)

        val retrieved = dao.getEntryByIdOnce("test-uuid-001")
        assertNotNull(retrieved)
        assertEquals("test-uuid-001", retrieved?.id)
        assertEquals("Superando la ansiedad matutina", retrieved?.title)
        assertEquals("ENC_SECURE::U2VjcmV0Q29udGVudA==", retrieved?.encryptedContent)
        assertEquals("Hoy sentí un nudo en el pecho al despertar.", retrieved?.plainContent)
        assertEquals(3, retrieved?.tags?.size)
        assertTrue(retrieved?.tags?.contains("ansiedad") == true)
        assertEquals("🌱", retrieved?.moodEmoji)
        assertEquals("ViajeroZen", retrieved?.authorAlias)
        assertEquals("user_abc_123", retrieved?.userId)
    }

    @Test
    fun flowReturnsAllEntriesInDescendingOrder() = runBlocking {
        val entry1 = EncryptedDiaryEntryEntity(
            id = "1",
            title = "Primera entrada",
            encryptedContent = "ENC1",
            timestamp = 1000L
        )
        val entry2 = EncryptedDiaryEntryEntity(
            id = "2",
            title = "Segunda entrada más reciente",
            encryptedContent = "ENC2",
            timestamp = 2000L
        )

        dao.insertEntries(listOf(entry1, entry2))

        val allEntries = dao.getAllEntries().first()
        assertEquals(2, allEntries.size)
        assertEquals("2", allEntries[0].id)
        assertEquals("1", allEntries[1].id)
    }

    @Test
    fun searchEntriesFindsMatchingKeyword() = runBlocking {
        val entry = EncryptedDiaryEntryEntity(
            id = "search_test_id",
            title = "Respiración 4-7-8 al anochecer",
            encryptedContent = "ENC_SEARCH",
            plainContent = "La técnica me ayudó a conciliar el sueño.",
            tags = listOf("sueño", "paz")
        )
        dao.insertEntry(entry)

        val results = dao.searchEntries("anochecer").first()
        assertEquals(1, results.size)
        assertEquals("search_test_id", results[0].id)
    }

    @Test
    fun deleteEntryRemovesItFromDatabase() = runBlocking {
        val entry = EncryptedDiaryEntryEntity(
            id = "delete_target",
            title = "Nota temporal",
            encryptedContent = "ENC_TEMP"
        )
        dao.insertEntry(entry)
        assertEquals(1, dao.getEntryCount().first())

        dao.deleteEntryById("delete_target")
        val retrieved = dao.getEntryByIdOnce("delete_target")
        assertNull(retrieved)
        assertEquals(0, dao.getEntryCount().first())
    }
}
