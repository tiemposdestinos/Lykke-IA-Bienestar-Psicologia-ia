package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.SectionCategory
import com.example.data.repository.LykkeRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context matches app name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Asesorías y Consultorías Lykke", appName)
    }

    @Test
    fun `repository provides questions for all sections`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = LykkeRepository(context)

        val stressQuestions = repo.getQuestionsForCategory(SectionCategory.STRESS)
        val frustrationQuestions = repo.getQuestionsForCategory(SectionCategory.FRUSTRATION)
        val goalsQuestions = repo.getQuestionsForCategory(SectionCategory.GOALS_DREAMS)
        val selfCareQuestions = repo.getQuestionsForCategory(SectionCategory.SELF_CARE)

        assertTrue(stressQuestions.isNotEmpty())
        assertTrue(frustrationQuestions.isNotEmpty())
        assertTrue(goalsQuestions.isNotEmpty())
        assertTrue(selfCareQuestions.isNotEmpty())

        val firstStress = stressQuestions.first()
        assertTrue(firstStress.options.isNotEmpty())
        assertNotNull(firstStress.options.first().psychologicalComment)
    }

    @Test
    fun `repository manages sessions and milestone tracking`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = LykkeRepository(context)

        val initial = repo.sessionCount.value
        repo.incrementSession("Prueba unitaria de sesión")
        assertEquals(initial + 1, repo.sessionCount.value)
    }

    @Test
    fun `repository handles encrypted diary entries and gamification XP`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = LykkeRepository(context)

        val initialCount = repo.diaryEntries.value.size
        val initialXp = repo.totalXp.value

        repo.addDiaryEntry(
            title = "Test Relato Privado",
            content = "Contenido confidencial sobre ansiedad",
            tags = listOf("ansiedad", "metas"),
            mood = "🌱",
            authorAlias = "Tester"
        )

        assertEquals(initialCount + 1, repo.diaryEntries.value.size)
        assertTrue(repo.totalXp.value > initialXp)

        val added = repo.diaryEntries.value.first()
        assertTrue(added.encryptedContent.startsWith("ENC_"))
        assertEquals("Test Relato Privado", added.title)
    }
}
