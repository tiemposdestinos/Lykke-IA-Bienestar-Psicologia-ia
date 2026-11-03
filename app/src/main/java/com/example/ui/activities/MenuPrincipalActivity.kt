package com.example.ui.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import com.example.MainActivity
import com.example.R

class MenuPrincipalActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal)

        // 1. Vinculación del botón principal del Club y los botones secundarios
        val btnClubConsultoria = findViewById<LinearLayout>(R.id.btnClubConsultoria)
        val btnInsigniasDiplomas = findViewById<LinearLayout>(R.id.btnInsigniasDiplomas)
        val btnDonaciones = findViewById<LinearLayout>(R.id.btnDonarSoporte)
        val btnCanjear = findViewById<Button>(R.id.btnCanjearCodigo)

        // ACCIÓN 1: Club de Consultoría Ilimitada (Desprende Diario Seguro y Chat de Desahogo)
        btnClubConsultoria?.setOnClickListener {
            mostrarOpcionesClubConsultoria()
        }

        // ACCIÓN 2: Insignias y Diplomas (Abre directamente la pasarela para trámite de diploma)
        btnInsigniasDiplomas?.setOnClickListener {
            abrirPasarelaPago("diploma")
        }

        // ACCIÓN 3: Aportación Voluntaria Libre (Abre la pasarela para donación)
        btnDonaciones?.setOnClickListener {
            abrirPasarelaPago("donacion")
        }

        // ACCIÓN 4: Canje directo de clave de activación
        btnCanjear?.setOnClickListener {
            val intent = Intent(this, CodigoActivacionActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Muestra el menú emergente del Club de Consultoría Ilimitada
     * desde el cual se desprenden el Diario Seguro y el Chat de Desahogo.
     */
    private fun mostrarOpcionesClubConsultoria() {
        val sharedPreferences = getSharedPreferences("ControlMódulos", Context.MODE_PRIVATE)
        val esMiembro = sharedPreferences.getBoolean("miembro_club_consultoria", false)
        val usos = sharedPreferences.getInt("total_usos_gratis", 0)

        val opciones = arrayOf(
            "📖 Diario Seguro (Escritura privada y encriptada)",
            "💬 Chat de Desahogo (Acompañamiento reflexivo)",
            "🔑 Canjear Clave de Activación"
        )

        val titulo = if (esMiembro) {
            "Club de Consultoría Ilimitada (Pase Activo ✨)"
        } else {
            "Club de Consultoría Ilimitada ($usos/5 usos)"
        }

        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> {
                        evaluarAccesoMapeado("consultoria") {
                            abrirPantallaDiarioSeguroReal()
                        }
                    }
                    1 -> {
                        evaluarAccesoMapeado("consultoria") {
                            abrirPantallaChatDesahogoReal()
                        }
                    }
                    2 -> {
                        val intent = Intent(this, CodigoActivacionActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    /**
     * Evalúa el acceso de los 5 usos compartidos con SharedPreferences ("ControlMódulos").
     * Si 'miembro_club_consultoria' es true, salta la restricción permanentemente.
     */
    private fun evaluarAccesoMapeado(tipoPago: String, onPermitido: () -> Unit) {
        val sharedPreferences = getSharedPreferences("ControlMódulos", Context.MODE_PRIVATE)
        val esMiembro = sharedPreferences.getBoolean("miembro_club_consultoria", false)

        // Si ya es miembro vitalicio, tiene acceso ilimitado
        if (esMiembro) {
            onPermitido()
            return
        }

        val usosActuales = sharedPreferences.getInt("total_usos_gratis", 0)

        if (usosActuales >= 5) {
            // Límite alcanzado: Bloquea y abre la pasarela de pago con el tipo correspondiente
            abrirPasarelaPago(tipoPago)
        } else {
            // Incrementa el contador compartido por 1 y permite el acceso al módulo
            sharedPreferences.edit()
                .putInt("total_usos_gratis", usosActuales + 1)
                .apply()
            onPermitido()
        }
    }

    private fun abrirPasarelaPago(tipoPago: String) {
        val intent = Intent(this, PantallaWebPagoActivity::class.java).apply {
            putExtra("TIPO_PAGO", tipoPago)
        }
        startActivity(intent)
    }

    private fun abrirPantallaDiarioSeguroReal() {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("SCREEN_TARGET", "DIARY")
        }
        startActivity(intent)
    }

    private fun abrirPantallaChatDesahogoReal() {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("SCREEN_TARGET", "CHAT")
        }
        startActivity(intent)
    }
}
