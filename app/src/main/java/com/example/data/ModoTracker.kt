package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.ChronoUnit

// Inicialización de la extensión de DataStore en el Contexto de Android
val Context.dataStoreTracker by preferencesDataStore(name = "lykke_wellness_tracker")

class ModoTracker(private val context: Context) {

    companion object {
        val KEY_RACHA_ACTUAL = intPreferencesKey("racha_actual")
        val KEY_ULTIMA_FECHA_COMPLETADO = stringPreferencesKey("ultima_fecha_completado")
        val KEY_MAXIMA_RACHA = intPreferencesKey("maxima_racha")
    }

    // 1. OBTENER ESTADÍSTICAS: Flujo para leer los datos en tiempo real en tu UI
    val rachaActualFlow: Flow<Int> = context.dataStoreTracker.data.map { preferences ->
        preferences[KEY_RACHA_ACTUAL] ?: 0
    }

    val maximaRachaFlow: Flow<Int> = context.dataStoreTracker.data.map { preferences ->
        preferences[KEY_MAXIMA_RACHA] ?: 0
    }

    /**
     * 2. REGISTRAR ACTIVIDAD DIARIA: Lógica para calcular si la racha continúa,
     * se mantiene, o se rompe si pasó más de un día.
     */
    suspend fun registrarActividadCompletada() {
        val hoy = LocalDate.now()

        context.dataStoreTracker.edit { preferences ->
            val ultimaFechaString = preferences[KEY_ULTIMA_FECHA_COMPLETADO]
            val rachaActual = preferences[KEY_RACHA_ACTUAL] ?: 0
            val maximaRacha = preferences[KEY_MAXIMA_RACHA] ?: 0

            if (ultimaFechaString == null) {
                // Primera vez que usa la app: Inicia la racha en 1
                preferences[KEY_RACHA_ACTUAL] = 1
                preferences[KEY_ULTIMA_FECHA_COMPLETADO] = hoy.toString()
                if (maximaRacha < 1) preferences[KEY_MAXIMA_RACHA] = 1
            } else {
                val ultimaFecha = LocalDate.parse(ultimaFechaString)
                val diasDeDiferencia = ChronoUnit.DAYS.between(ultimaFecha, hoy)

                when (diasDeDiferencia) {
                    0L -> {
                        // Ya completó una actividad hoy, la racha se mantiene igual
                    }
                    1L -> {
                        // Fue ayer: ¡La racha continúa! Aumenta 1
                        val nuevaRacha = rachaActual + 1
                        preferences[KEY_RACHA_ACTUAL] = nuevaRacha
                        preferences[KEY_ULTIMA_FECHA_COMPLETADO] = hoy.toString()
                        if (nuevaRacha > maximaRacha) {
                            preferences[KEY_MAXIMA_RACHA] = nuevaRacha
                        }
                    }
                    else -> {
                        // Pasaron 2 días o más: La racha se rompió y vuelve a empezar en 1
                        preferences[KEY_RACHA_ACTUAL] = 1
                        preferences[KEY_ULTIMA_FECHA_COMPLETADO] = hoy.toString()
                    }
                }
            }
        }
    }

    /**
     * 3. VERIFICAR RACHA AL ABRIR LA APP: Resetea a 0 la racha si el usuario
     * olvidó ingresar el día de ayer.
     */
    suspend fun verificarSiRachaSeRompio() {
        val hoy = LocalDate.now()
        context.dataStoreTracker.edit { preferences ->
            val ultimaFechaString = preferences[KEY_ULTIMA_FECHA_COMPLETADO]
            if (ultimaFechaString != null) {
                val ultimaFecha = LocalDate.parse(ultimaFechaString)
                val diasDeDiferencia = ChronoUnit.DAYS.between(ultimaFecha, hoy)

                // Si pasó más de un día sin hacer nada, la racha actual vuelve a 0
                if (diasDeDiferencia > 1L) {
                    preferences[KEY_RACHA_ACTUAL] = 0
                }
            }
        }
    }
}
