package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp

data class UserProfile(
    @DocumentId val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val adminNotificationEmail: String = "tiemposdestinos@gmail.com",
    val syncStatus: String = "Registrado y Notificado a tiemposdestinos@gmail.com",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val lastLoginAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>(
            "userId" to userId,
            "displayName" to displayName,
            "email" to email,
            "adminNotificationEmail" to adminNotificationEmail,
            "syncStatus" to syncStatus,
            "createdAt" to (createdAt ?: FieldValue.serverTimestamp()),
            "lastLoginAt" to FieldValue.serverTimestamp()
        )
        if (photoUrl != null) {
            map["photoUrl"] = photoUrl
        }
        return map
    }
}
