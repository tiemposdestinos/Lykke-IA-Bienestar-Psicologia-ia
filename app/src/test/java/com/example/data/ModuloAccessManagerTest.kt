package com.example.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ModuloAccessManagerTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        ModuloAccessManager.reiniciarContadorParaPruebas(context)
    }

    @Test
    fun testContadorIncrementaHastaCincoUsos() {
        assertEquals(0, ModuloAccessManager.obtenerUsosGratis(context))
        assertFalse(ModuloAccessManager.esMiembroClub(context))

        var permitidos = 0
        for (i in 1..5) {
            ModuloAccessManager.evaluarAccesoMapeado(context, "consultoria") {
                permitidos++
            }
            assertEquals(i, ModuloAccessManager.obtenerUsosGratis(context))
            assertEquals(i, permitidos)
        }

        // El 6to uso debe estar bloqueado (permitidos sigue siendo 5)
        ModuloAccessManager.evaluarAccesoMapeado(context, "consultoria") {
            permitidos++
        }
        assertEquals(5, permitidos)
        assertEquals(5, ModuloAccessManager.obtenerUsosGratis(context))
    }

    @Test
    fun testCanjeCodigoValidoDesbloqueaAccesoIlimitado() {
        assertFalse(ModuloAccessManager.canjearCodigo(context, "INVALIDO_123"))
        assertFalse(ModuloAccessManager.esMiembroClub(context))

        val exito = ModuloAccessManager.canjearCodigo(context, "CLUB2026X")
        assertTrue(exito)
        assertTrue(ModuloAccessManager.esMiembroClub(context))

        // Al ser miembro, nunca bloquea aunque se llame múltiples veces
        var accesos = 0
        for (i in 1..10) {
            ModuloAccessManager.evaluarAccesoMapeado(context, "consultoria") {
                accesos++
            }
        }
        assertEquals(10, accesos)
    }

    @Test
    fun testCanjeCodigosIntercaladosLivs() {
        // Verifica que las claves intercaladas con LIVS sean válidas (case-insensitive)
        val codigoLivs1 = "LIVS-3157-VIP"
        val codigoLivs2 = "livs-8964-cons" // minúsculas
        val codigoLivs3 = "LIVS-4890-WELL"

        assertTrue(ModuloAccessManager.canjearCodigo(context, codigoLivs1))
        assertTrue(ModuloAccessManager.esMiembroClub(context))

        ModuloAccessManager.reiniciarContadorParaPruebas(context)
        assertFalse(ModuloAccessManager.esMiembroClub(context))

        assertTrue(ModuloAccessManager.canjearCodigo(context, codigoLivs2))
        assertTrue(ModuloAccessManager.esMiembroClub(context))

        ModuloAccessManager.reiniciarContadorParaPruebas(context)
        assertTrue(ModuloAccessManager.canjearCodigo(context, codigoLivs3))
        assertTrue(ModuloAccessManager.esMiembroClub(context))
    }

    @Test
    fun testEstructuraVeinteCodigosCadaTerceroTieneLivs() {
        val llaves = com.example.ui.activities.CodigoActivacionActivity.CODIGOS_LLAVE_OCULTOS
        assertEquals(20, llaves.size)

        for (i in 0 until 20) {
            val posicionUnoIndexada = i + 1
            val codigo = llaves[i]
            if (posicionUnoIndexada % 3 == 0) {
                // Posiciones 3, 6, 9, 12, 15, 18 DEBEN contener "LIVS"
                assertTrue(
                    "El código en posición $posicionUnoIndexada ($codigo) debe contener 'LIVS'",
                    codigo.contains("LIVS", ignoreCase = true)
                )
            } else {
                // Las demás posiciones NO deben contener "LIVS"
                assertFalse(
                    "El código en posición $posicionUnoIndexada ($codigo) no debe contener 'LIVS'",
                    codigo.contains("LIVS", ignoreCase = true)
                )
            }
        }
    }
}
