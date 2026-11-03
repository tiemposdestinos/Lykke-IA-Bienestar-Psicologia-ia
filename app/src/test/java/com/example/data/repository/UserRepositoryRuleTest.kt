package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class UserRepositoryRuleTest : FirestoreEmulatorTestBase() {

    private lateinit var repository: UserRepository

    @Before
    fun setUpRepo() {
        repository = UserRepository(firestore, auth)
    }

    @Test
    fun authenticatedUser_canCreateAndReadOwnProfile() = runTest {
        val userEmail = "testuser_${System.currentTimeMillis()}@example.com"
        val uid = signInTestUser(userEmail)
        val currentUser = auth.currentUser
        assertNotNull("Current user must be signed in", currentUser)

        // Sync/Create profile
        val profile = repository.syncOrCreateUserProfile(currentUser!!, "Paciente Sereno")
        assertEquals(uid, profile.userId)
        assertEquals("Paciente Sereno", profile.displayName)
        assertEquals(userEmail, profile.email)
        assertEquals("tiemposdestinos@gmail.com", profile.adminNotificationEmail)

        // Read directly from Firestore to verify persistence and rule compliance
        val snapshot = firestore.collection("users").document(uid).get().await()
        assertTrue("User document should exist", snapshot.exists())
        assertEquals(uid, snapshot.getString("userId"))
        assertEquals("tiemposdestinos@gmail.com", snapshot.getString("adminNotificationEmail"))
    }

    @Test
    fun unauthenticatedUser_cannotReadProfile() = runTest {
        auth.signOut()
        val randomUid = "unauth_${System.currentTimeMillis()}"

        try {
            firestore.collection("users").document(randomUid).get().await()
            fail("Expected FirebaseFirestoreException PERMISSION_DENIED")
        } catch (e: Exception) {
            assertTrue("Expected permission denied error", e.message?.contains("PERMISSION_DENIED") == true || e is FirebaseFirestoreException)
        }
    }

    @Test
    fun crossUser_cannotReadOtherUserProfile() = runTest {
        val user1Email = "user1_${System.currentTimeMillis()}@example.com"
        val user2Email = "user2_${System.currentTimeMillis()}@example.com"

        val uid1 = signInTestUser(user1Email)
        val user1 = auth.currentUser!!
        repository.syncOrCreateUserProfile(user1, "Usuario Uno")

        // Switch to user 2
        val uid2 = signInTestUser(user2Email)
        assertNotEquals(uid1, uid2)

        try {
            firestore.collection("users").document(uid1).get().await()
            fail("Expected FirebaseFirestoreException PERMISSION_DENIED when reading another user's document")
        } catch (e: Exception) {
            assertTrue("Expected permission denied error", e.message?.contains("PERMISSION_DENIED") == true || e is FirebaseFirestoreException)
        }
    }
}
