package com.example.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Preferencias persistentes blindadas para personalización del usuario,
 * nombre de la IA, nombre del usuario y contador de auto-regalos de amor propio.
 */
class PreferenciasPersistentes(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_preferencias_blindadas", Context.MODE_PRIVATE)

    var nombreIA: String
        get() = prefs.getString("KEY_NOMBRE_IA", "Guardián Conciencia") ?: "Guardián Conciencia"
        set(value) = prefs.edit().putString("KEY_NOMBRE_IA", value).apply()

    var nombreUsuario: String
        get() = prefs.getString("KEY_NOMBRE_USUARIO", "") ?: ""
        set(value) = prefs.edit().putString("KEY_NOMBRE_USUARIO", value).apply()

    var contadorRegalos: Int
        get() = prefs.getInt("KEY_TOTAL_REGALOS", 0)
        set(value) = prefs.edit().putInt("KEY_TOTAL_REGALOS", value).apply()

    fun incrementarRegalo(): Int {
        val total = contadorRegalos + 1
        contadorRegalos = total
        return total
    }

    companion object {
        @Volatile
        private var INSTANCE: PreferenciasPersistentes? = null

        fun getInstance(context: Context): PreferenciasPersistentes {
            return INSTANCE ?: synchronized(this) {
                val instance = PreferenciasPersistentes(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
