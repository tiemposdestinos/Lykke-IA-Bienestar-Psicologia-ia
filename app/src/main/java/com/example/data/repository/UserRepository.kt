package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    // Convenience constructor resolving custom database ID from XML resources
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        Firebase.auth
    )

    private companion object {
        const val TAG = "UserRepository"
        const val COLLECTION_USERS = "users"
        const val ADMIN_EMAIL = "tiemposdestinos@gmail.com"
    }

    /**
     * Observes the active user's profile from Firestore in real-time.
     */
    fun observeUserProfile(userId: String): Flow<UserProfile?> = callbackFlow {
        val docRef = db.collection(COLLECTION_USERS).document(userId)
        val listener: ListenerRegistration = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Error observing user profile: ${error.message}", error)
                trySend(null)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val profile = snapshot.toObject(UserProfile::class.java)
                trySend(profile)
            } else {
                trySend(null)
            }
        }

        awaitClose {
            listener.remove()
        }
    }

    /**
     * Synchronizes or creates user account in Firestore upon Google Sign-In.
     * Generates the record for tiemposdestinos@gmail.com without requiring age or gender.
     */
    suspend fun syncOrCreateUserProfile(user: FirebaseUser, customAlias: String? = null): UserProfile {
        val uid = user.uid
        val docRef = db.collection(COLLECTION_USERS).document(uid)
        val snapshot = docRef.get().await()

        val chosenName = when {
            !customAlias.isNullOrBlank() -> customAlias.trim()
            !user.displayName.isNullOrBlank() -> user.displayName!!.trim()
            else -> "Usuario Confidencial"
        }
        val userEmail = user.email ?: "usuario@livsformlykke.com"
        val photoUrl = user.photoUrl?.toString()

        if (snapshot.exists()) {
            // Update last login and keep existing createdAt
            val existing = snapshot.toObject(UserProfile::class.java)
            val updatedMap = mutableMapOf<String, Any>(
                "userId" to uid,
                "displayName" to (existing?.displayName?.ifBlank { chosenName } ?: chosenName),
                "email" to userEmail,
                "adminNotificationEmail" to ADMIN_EMAIL,
                "syncStatus" to "Cuenta Sincronizada con base de datos de tiemposdestinos@gmail.com",
                "createdAt" to (snapshot.getTimestamp("createdAt") ?: FieldValue.serverTimestamp()),
                "lastLoginAt" to FieldValue.serverTimestamp()
            )
            if (photoUrl != null) {
                updatedMap["photoUrl"] = photoUrl
            }
            docRef.update(updatedMap).await()

            return existing?.copy(
                displayName = updatedMap["displayName"] as String,
                lastLoginAt = null
            ) ?: UserProfile(
                userId = uid,
                displayName = chosenName,
                email = userEmail,
                photoUrl = photoUrl,
                adminNotificationEmail = ADMIN_EMAIL,
                syncStatus = "Cuenta Sincronizada con base de datos de tiemposdestinos@gmail.com"
            )
        } else {
            // New user registration without requiring age or gender
            val newProfileMap = mutableMapOf<String, Any>(
                "userId" to uid,
                "displayName" to chosenName,
                "email" to userEmail,
                "adminNotificationEmail" to ADMIN_EMAIL,
                "syncStatus" to "Nueva cuenta registrada y notificada a tiemposdestinos@gmail.com",
                "createdAt" to FieldValue.serverTimestamp(),
                "lastLoginAt" to FieldValue.serverTimestamp()
            )
            if (photoUrl != null) {
                newProfileMap["photoUrl"] = photoUrl
            }
            docRef.set(newProfileMap).await()

            Log.i(TAG, "Nueva cuenta creada y respaldada para $ADMIN_EMAIL (UID: $uid, Email: $userEmail)")

            return UserProfile(
                userId = uid,
                displayName = chosenName,
                email = userEmail,
                photoUrl = photoUrl,
                adminNotificationEmail = ADMIN_EMAIL,
                syncStatus = "Nueva cuenta registrada y notificada a tiemposdestinos@gmail.com"
            )
        }
    }

    /**
     * Updates user's chosen display alias.
     */
    suspend fun updateDisplayName(userId: String, newName: String) {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        require(newName.isNotBlank()) { "Display name must not be blank" }
        val docRef = db.collection(COLLECTION_USERS).document(userId)
        docRef.update(
            mapOf(
                "displayName" to newName.trim(),
                "lastLoginAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }
}
