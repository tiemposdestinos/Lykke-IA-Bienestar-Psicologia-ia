package com.example.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Monitor reactivo de conectividad para la app Lykke.
 * Emite continuamente true si hay conexión activa a internet y false si se pierde.
 */
class LykkeNetworkMonitor(context: Context) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    val estaConectado: Flow<Boolean> = callbackFlow {
        if (connectivityManager == null) {
            trySend(true)
            awaitClose {}
            return@callbackFlow
        }

        // Estado inicial
        val activeNetwork = connectivityManager.activeNetwork
        val initialCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        val initialConnected = initialCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        trySend(initialConnected)

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(false)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        awaitClose {
            try {
                connectivityManager.unregisterNetworkCallback(callback)
            } catch (_: Exception) {}
        }
    }
}
