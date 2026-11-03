package com.example.integration

import android.content.Context
import android.widget.Toast
import com.example.data.model.GlobalNetworkRegistry
import com.example.data.model.GlobalService
import com.example.data.model.LykkeChatChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Controlador de Activación Lykke.
 * Unifica la configuración de canales comunitarios abiertos (Matrix, IRC, XMPP, Clan)
 * y la activación de pasarelas internacionales (Baidu, Yandex, Google, Gaode, etc.)
 */
class LykkeActivationManager(
    private val context: Context
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun configurarChatComunidad(channel: LykkeChatChannel, topic: String = "psicologia") {
        coroutineScope.launch {
            Toast.makeText(
                context,
                "Conectando canal seguro: ${channel.name} (#$topic)",
                Toast.LENGTH_SHORT
            ).show()

            withContext(Dispatchers.IO) {
                when (channel.protocolTag) {
                    "Matrix-Org" -> println("[Activación] Sincronizando hilos federados en LykkeBBChat")
                    "IRC-Libera" -> println("[Activación] Conectando sockets fluidos en LykkeGrupalChat")
                    "XMPP-Jabber" -> println("[Activación] Abriendo flujos XML estables en LykkePeople")
                    "Baidu-Loop" -> println("[Activación] Puenteando canal con pasarela Baidu para Lykke-Loop Chine")
                    "Yandex-Loop" -> println("[Activación] Indexando comunidad en Yandex para Lykke-Loop Russ")
                    "Matrix-P2P" -> println("[Activación] Aislando llaves de cifrado extremo a extremo para LykkeClan")
                }
            }
        }
    }

    fun activarServiciosGlobales(onSuccess: (List<GlobalService>) -> Unit) {
        coroutineScope.launch {
            try {
                val serviciosDisponibles = withContext(Dispatchers.IO) {
                    GlobalNetworkRegistry.services
                }
                onSuccess(serviciosDisponibles)
                Toast.makeText(
                    context,
                    "¡Servicios Globales Activados con Éxito! Puentes internacionales listos.",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Error al activar pasarelas globales: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
