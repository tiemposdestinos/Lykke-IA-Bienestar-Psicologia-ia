package com.example.ui.activities

import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import com.example.R
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CodigoActivacionActivityTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        val prefs = context.getSharedPreferences("ControlMódulos", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    @Test
    fun testCodigoInvalidoMuestraErrorYNoActivaMembresia() {
        val controller = Robolectric.buildActivity(CodigoActivacionActivity::class.java).setup()
        val activity = controller.get()

        val etCodigo = activity.findViewById<EditText>(R.id.etCodigoActivacion)
        val btnValidar = activity.findViewById<Button>(R.id.btnValidarCodigo)
        val tvResultado = activity.findViewById<TextView>(R.id.tvMensajeResultado)

        assertNotNull(etCodigo)
        assertNotNull(btnValidar)
        assertNotNull(tvResultado)

        // Ingresar código erróneo
        etCodigo.setText("CLAVE-FALSA-000")
        btnValidar.performClick()

        val prefs = activity.getSharedPreferences("ControlMódulos", Context.MODE_PRIVATE)
        assertFalse(prefs.getBoolean("miembro_club_consultoria", false))
        assertTrue(tvResultado.text.toString().contains("inválida", ignoreCase = true))
    }

    @Test
    fun testCodigoValidoIntercaladoGuardaMiembroClubConsultoriaTrue() {
        val controller = Robolectric.buildActivity(CodigoActivacionActivity::class.java).setup()
        val activity = controller.get()

        val etCodigo = activity.findViewById<EditText>(R.id.etCodigoActivacion)
        val btnValidar = activity.findViewById<Button>(R.id.btnValidarCodigo)
        val tvResultado = activity.findViewById<TextView>(R.id.tvMensajeResultado)

        // Código 3 de la lista (con Livs)
        etCodigo.setText("livs-3157-vip")
        btnValidar.performClick()

        val prefs = activity.getSharedPreferences("ControlMódulos", Context.MODE_PRIVATE)
        assertTrue(prefs.getBoolean("miembro_club_consultoria", false))
        assertTrue(tvResultado.text.toString().contains("correcta", ignoreCase = true))
    }

    @Test
    fun testCodigoSinLivsDeLaListaValidaExitosamente() {
        val controller = Robolectric.buildActivity(CodigoActivacionActivity::class.java).setup()
        val activity = controller.get()

        val etCodigo = activity.findViewById<EditText>(R.id.etCodigoActivacion)
        val btnValidar = activity.findViewById<Button>(R.id.btnValidarCodigo)

        // Código 1 de la lista (sin Livs)
        etCodigo.setText("ALPHA-9284-SEC")
        btnValidar.performClick()

        val prefs = activity.getSharedPreferences("ControlMódulos", Context.MODE_PRIVATE)
        assertTrue(prefs.getBoolean("miembro_club_consultoria", false))
    }
}
