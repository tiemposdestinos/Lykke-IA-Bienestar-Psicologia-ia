package com.example.ui.activities

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.R
import com.example.data.ModuloAccessManager

class CodigoActivacionActivity : ComponentActivity() {

    companion object {
        const val PREFS_NAME = "ControlMódulos"
        const val KEY_MIEMBRO_CLUB = "miembro_club_consultoria"

        /**
         * Lista privada y oculta de 20 códigos llave autorizados.
         * Cada tercer código (posiciones 3, 6, 9, 12, 15, 18) contiene intercalada la palabra 'LIVS' / 'Livs'.
         * Esta lista permanece protegida en el código fuente y NO es visible en la interfaz de usuario.
         */
        val CODIGOS_LLAVE_OCULTOS: List<String> = listOf(
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
            "CLUB-8219-ZEN"      // 20
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_codigo_activacion)

        // 1. Vinculación del campo EditText y los botones
        val etCodigo = findViewById<EditText>(R.id.etCodigoActivacion)
        val btnValidar = findViewById<Button>(R.id.btnValidarCodigo)
        val btnVolver = findViewById<Button>(R.id.btnVolverCodigo)
        val tvResultado = findViewById<TextView>(R.id.tvMensajeResultado)

        btnVolver?.setOnClickListener {
            finish()
        }

        // 2. Acción de validación
        btnValidar?.setOnClickListener {
            val codigoIngresado = etCodigo.text.toString().trim()

            if (codigoIngresado.isEmpty()) {
                mostrarMensaje(
                    tvResultado,
                    "Por favor, ingresa tu clave de activación.",
                    esError = true
                )
                return@setOnClickListener
            }

            if (verificarCodigo(codigoIngresado)) {
                // Éxito: Guardar miembro_club_consultoria = true en SharedPreferences ("ControlMódulos")
                val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                prefs.edit().putBoolean(KEY_MIEMBRO_CLUB, true).apply()

                mostrarMensaje(
                    tvResultado,
                    "✓ ¡Clave correcta! Membresía activada de forma ilimitada.",
                    esError = false
                )

                Toast.makeText(
                    this,
                    "¡Bienvenido al Club de Consultoría Ilimitada!",
                    Toast.LENGTH_LONG
                ).show()

                btnValidar.isEnabled = false
                etCodigo.isEnabled = false
                etCodigo.postDelayed({
                    finish()
                }, 1200)
            } else {
                mostrarMensaje(
                    tvResultado,
                    "Clave inválida o expirada. Verifica el código enviado a tu correo.",
                    esError = true
                )
            }
        }
    }

    /**
     * Verifica si el código ingresado coincide con alguna de las 20 llaves ocultas
     * (de forma insensible a mayúsculas/minúsculas).
     */
    fun verificarCodigo(codigo: String): Boolean {
        val limpio = codigo.trim().uppercase()
        // Valida contra las 20 llaves intercaladas o el gestor de accesos
        val coincideEnListaOculta = CODIGOS_LLAVE_OCULTOS.any { it.equals(limpio, ignoreCase = true) }
        val coincideEnModulo = ModuloAccessManager.CODIGOS_ACTIVACION_VALIDOS.any { it.equals(limpio, ignoreCase = true) }
        return coincideEnListaOculta || coincideEnModulo
    }

    private fun mostrarMensaje(tv: TextView?, texto: String, esError: Boolean) {
        tv?.apply {
            this.text = texto
            this.visibility = View.VISIBLE
            // Verde brillante (#4ADE80) para éxito, Rojo (#EF4444) para error
            this.setTextColor(if (esError) 0xFFEF4444.toInt() else 0xFF4ADE80.toInt())
        }
    }
}
