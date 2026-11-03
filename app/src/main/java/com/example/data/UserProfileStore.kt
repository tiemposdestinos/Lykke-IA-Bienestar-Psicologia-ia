package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStorePerfil by preferencesDataStore(name = "lykke_user_profile")

data class PerfilUsuario(
    val nombre: String = "",
    val apodoConciencia: String = "Conciencia",
    val jornadaPrincipal: String = "Trabajo", // "Trabajo", "Estudios", "Deporte", "Hogar"
    val hobbyPrincipal: String = "Caminar y leer",
    val actividadEspiritual: String = "Meditar", // "Ir a la iglesia", "Meditar", "Ninguna"
    val tiempoReinvencionMinutos: Int = 15
) {
    // Aliases para máxima compatibilidad con las diferentes llamadas de UI
    val nombreUsuario: String get() = nombre
}

class UserProfileStore(private val context: Context) {

    companion object {
        private val KEY_NOMBRE = stringPreferencesKey("user_name")
        private val KEY_APODO_CONCIENCIA = stringPreferencesKey("user_apodo_conciencia")
        private val KEY_JORNADA = stringPreferencesKey("user_jornada")
        private val KEY_HOBBY = stringPreferencesKey("user_hobby")
        private val KEY_ESPIRITUAL = stringPreferencesKey("user_espiritual")
        private val KEY_TIEMPO_REINVENCION = intPreferencesKey("user_tiempo_reinvencion")
        private val KEY_ESTADO_ONBOARDING = stringPreferencesKey("onboarding_state") // "PREGUNTANDO_NOMBRE", "COMPLETO"

        val SUGERENCIAS_NOMBRES = listOf(
            "Conciencia", "Mamatina", "Luis Lykke", "Caeli", "Xi", "Fiel", "lud", "None", "sha (Саша)", "Kai", "Haven", "Zen"
        )
    }

    val perfilFlow: Flow<PerfilUsuario> = context.dataStorePerfil.data.map { pref ->
        PerfilUsuario(
            nombre = pref[KEY_NOMBRE] ?: "",
            apodoConciencia = pref[KEY_APODO_CONCIENCIA] ?: "Conciencia",
            jornadaPrincipal = pref[KEY_JORNADA] ?: "Trabajo",
            hobbyPrincipal = pref[KEY_HOBBY] ?: "Caminar y leer",
            actividadEspiritual = pref[KEY_ESPIRITUAL] ?: "Meditar",
            tiempoReinvencionMinutos = pref[KEY_TIEMPO_REINVENCION] ?: 15
        )
    }

    val estadoOnboardingFlow: Flow<String> = context.dataStorePerfil.data.map { pref ->
        pref[KEY_ESTADO_ONBOARDING] ?: "PREGUNTANDO_NOMBRE"
    }

    suspend fun guardarConfiguracion(nombreUsuario: String, apodoConciencia: String) {
        context.dataStorePerfil.edit { pref ->
            pref[KEY_NOMBRE] = nombreUsuario
            pref[KEY_APODO_CONCIENCIA] = apodoConciencia
            pref[KEY_ESTADO_ONBOARDING] = "COMPLETO"
        }
    }

    suspend fun guardarPerfil(perfil: PerfilUsuario) {
        context.dataStorePerfil.edit { pref ->
            pref[KEY_NOMBRE] = perfil.nombre
            pref[KEY_APODO_CONCIENCIA] = perfil.apodoConciencia
            pref[KEY_JORNADA] = perfil.jornadaPrincipal
            pref[KEY_HOBBY] = perfil.hobbyPrincipal
            pref[KEY_ESPIRITUAL] = perfil.actividadEspiritual
            pref[KEY_TIEMPO_REINVENCION] = perfil.tiempoReinvencionMinutos
            pref[KEY_ESTADO_ONBOARDING] = "COMPLETO"
        }
    }
}

object MascotaInteractivaEngine {

    /**
     * Genera recordatorios y conversaciones aleatorias personalizadas basadas en el perfil.
     */
    fun generarDialogoMascota(perfil: PerfilUsuario, idioma: String): String {
        val nombre = if (perfil.nombre.isBlank()) "amigo/a" else perfil.nombre
        val apodo = if (perfil.apodoConciencia.isBlank()) "Lykke" else perfil.apodoConciencia

        val opcionesMensajes = listOf(
            when (idioma) {
                "en" -> "Hey $nombre, I am your entity '$apodo'. I know today is a busy day with your ${perfil.jornadaPrincipal.lowercase()} schedule. Did you take a small breathing break yet?"
                "cak" -> "¡Alli $nombre! In '$apodo', ri a-na'oj richin ri uxlanem pa ri a-samaj. ¿Xatuxlan yan?"
                else -> "Hola $nombre, soy tu entidad '$apodo'. Sé que hoy tienes una jornada dedicada a tus ${perfil.jornadaPrincipal.lowercase()}. ¿Ya te tomaste un pequeño respiro para cuidar tu mente?"
            },
            when (idioma) {
                "en" -> "Amazing job, $nombre! You dedicated ${perfil.tiempoReinvencionMinutos} minutes to reinvent yourself. Your companion '$apodo' reminds you: the sky is the limit!"
                "cak" -> "¡Utzilaj samaj $nombre! Ruma ri $apodo xya' rutzil chi re ri a-k'aslemal."
                else -> "¡Gran trabajo, $nombre! Tu compañero '$apodo' te recuerda que hoy te propusiste dedicar ${perfil.tiempoReinvencionMinutos} minutos para reinventarte. ¡El cielo es el límite!"
            },
            when (idioma) {
                "en" -> "Hey $nombre, if you are going out for ${perfil.hobbyPrincipal.lowercase()} or to the supermarket, walk mindfully. You form part of the same fabric of life."
                else -> "Hola $nombre, si vas a salir a despejarte con tu actividad de ${perfil.hobbyPrincipal.lowercase()} o vas camino al súper, recuerda caminar con calma. ¡Formas parte del mismo tejido de vida!"
            },
            when (idioma) {
                "en" -> "$nombre, taking time for your spiritual space like ${perfil.actividadEspiritual.lowercase()} strengthens your inner light."
                else -> "$nombre, apartar tiempo para tu espacio espiritual y realizar acciones como ${perfil.actividadEspiritual.lowercase()} fortalece la luz interior de tus venas."
            }
        )

        return opcionesMensajes.random()
    }
}
