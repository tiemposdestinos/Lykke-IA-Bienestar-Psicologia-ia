package com.example.data

import android.content.Context
import android.content.Intent
import com.example.ui.activities.PantallaWebPagoActivity
import java.time.LocalDate

object ModuloAccessManager {
    const val PREFS_NAME = "ControlMódulos"
    const val KEY_TOTAL_USOS = "total_usos_gratis"
    const val KEY_FECHA_ULTIMO_USO = "fecha_ultimo_uso_diario"
    const val KEY_MIEMBRO_CLUB = "miembro_club_consultoria"
    const val MAX_USOS_GRATIS = 5

    // 20 Códigos secretos de activación con 'LIVS' intercalado cada 3er código (para envío por correo a clientes)
    val CODIGOS_ACTIVACION_VALIDOS = setOf(
        "ALPHA-9284-SEC",    // 1
        "CLUB-7142-GOLD",    // 2
        "LIVS-3157-VIP",     // 3 (Contiene 'Livs')
        "TERRA-6821-KEY",    // 4
        "CLUB-5049-PLUS",    // 5
        "LIVS-8964-CONS",    // 6 (Contiene 'Livs')
        "OASIS-4271-SAFE",   // 7
        "CLUB-1683-STAR",    // 8
        "LIVS-5930-CARE",    // 9 (Contiene 'Livs')
        "ZENITH-7429-ACT",   // 10
        "CLUB-3816-ELITE",   // 11
        "LIVS-6295-MIND",    // 12 (Contiene 'Livs')
        "HARMONY-8104-ACC",  // 13
        "CLUB-2571-PRO",     // 14
        "LIVS-9438-SOUL",    // 15 (Contiene 'Livs')
        "AURORA-3702-KEY",   // 16
        "CLUB-5163-PRIME",   // 17
        "LIVS-4890-WELL",    // 18 (Contiene 'Livs')
        "VALLEY-6375-SEC",   // 19
        "CLUB-8219-ZEN",     // 20
        // Compatibilidad previa
        "LIVS-ALPHA-9284",
        "CLUB-LIVS-3157",
        "VIP-LIVS-8219",
        "CLUB2026X",
        "CONSULTA99",
        "VIPDESAHOGO",
        "LYKKE2026"
    )

    fun esMiembroClub(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_MIEMBRO_CLUB, false)
    }

    /**
     * Verifica si ya pasó el cambio de día (medianoche / límite de día).
     * Si el día cambió, reinicia el contador de usos diarios compartidos a 0.
     * Retorna true si se acaba de resetear por cambio de día.
     */
    fun verificarYResetearLimiteDiario(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val hoy = LocalDate.now().toString()
        val fechaGuardada = prefs.getString(KEY_FECHA_ULTIMO_USO, null)

        if (fechaGuardada == null) {
            prefs.edit().putString(KEY_FECHA_ULTIMO_USO, hoy).apply()
            return false
        }

        if (fechaGuardada != hoy) {
            // Ya pasó el límite de día / cambio de hora de medianoche: resetear los 5 usos gratuitos diarios
            prefs.edit()
                .putString(KEY_FECHA_ULTIMO_USO, hoy)
                .putInt(KEY_TOTAL_USOS, 0)
                .apply()
            return true
        }
        return false
    }

    fun obtenerUsosGratis(context: Context): Int {
        verificarYResetearLimiteDiario(context)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_TOTAL_USOS, 0)
    }

    /**
     * Evalúa el acceso compartido diario para Diario Seguro y Chat de Desahogo.
     * Si el usuario es miembro activo, nunca bloquea ni suma al límite.
     * Si ya pasó el límite de día (medianoche), los 5 usos diarios se renuevan.
     * Si no es miembro y ya consumió los 5 usos gratuitos del día, bloquea y abre pasarela de pago.
     * Si aún tiene usos disponibles hoy, suma 1 al contador y permite el acceso.
     */
    fun evaluarAccesoMapeado(
        context: Context,
        tipoPago: String = "consultoria",
        onPermitido: () -> Unit
    ) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val esMiembro = prefs.getBoolean(KEY_MIEMBRO_CLUB, false)

        if (esMiembro) {
            onPermitido()
            return
        }

        // Auto-reset si cambió el día
        verificarYResetearLimiteDiario(context)

        val usos = prefs.getInt(KEY_TOTAL_USOS, 0)
        if (usos >= MAX_USOS_GRATIS) {
            abrirPasarelaPago(context, tipoPago)
        } else {
            val hoy = LocalDate.now().toString()
            prefs.edit()
                .putInt(KEY_TOTAL_USOS, usos + 1)
                .putString(KEY_FECHA_ULTIMO_USO, hoy)
                .apply()
            onPermitido()
        }
    }

    fun abrirPasarelaPago(context: Context, tipoPago: String) {
        val intent = Intent(context, PantallaWebPagoActivity::class.java).apply {
            putExtra("TIPO_PAGO", tipoPago)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    /**
     * Valida el código de activación y activa membresía ilimitada en SharedPreferences.
     */
    fun canjearCodigo(context: Context, codigoIngresado: String): Boolean {
        val cleanCode = codigoIngresado.trim().uppercase()
        if (CODIGOS_ACTIVACION_VALIDOS.contains(cleanCode)) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .putBoolean(KEY_MIEMBRO_CLUB, true)
                .apply()
            return true
        }
        return false
    }

    fun reiniciarContadorParaPruebas(context: Context) {
        val hoy = LocalDate.now().toString()
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putInt(KEY_TOTAL_USOS, 0)
            .putString(KEY_FECHA_ULTIMO_USO, hoy)
            .putBoolean(KEY_MIEMBRO_CLUB, false)
            .apply()
    }

    /**
     * Simula que ya pasó el límite de día forzando la fecha al día de ayer
     * para verificar la renovación automática inmediata.
     */
    fun forzarPasoLimiteDiaParaPruebas(context: Context) {
        val ayer = LocalDate.now().minusDays(1).toString()
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_FECHA_ULTIMO_USO, ayer)
            .putInt(KEY_TOTAL_USOS, MAX_USOS_GRATIS)
            .apply()
    }
}

