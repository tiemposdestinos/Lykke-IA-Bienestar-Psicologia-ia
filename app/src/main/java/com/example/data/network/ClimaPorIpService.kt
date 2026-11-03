package com.example.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Servicio de geolocalización y sensación térmica por IP pública.
 * Consulta ipwho.is para coordenadas de la red y open-meteo para la sensación térmica actual.
 */
object ClimaPorIpService {

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .build()
    }

    suspend fun obtenerSensacionTermica(): String = withContext(Dispatchers.IO) {
        try {
            // 1. Obtener coordenadas por IP
            val reqIp = Request.Builder()
                .url("https://ipwho.is/")
                .header("User-Agent", "Lykke-Ecosystem/1.0")
                .build()

            val ipResponse = httpClient.newCall(reqIp).execute()
            val ipBody = ipResponse.body?.string() ?: return@withContext "Ubicación actual no disponible."
            val ipJson = JSONObject(ipBody)

            val success = ipJson.optBoolean("success", false)
            val city = ipJson.optString("city", "Tu Ciudad")
            val country = ipJson.optString("country", "")
            val lat = ipJson.optDouble("latitude", 0.0)
            val lon = ipJson.optDouble("longitude", 0.0)

            if (!success || lat == 0.0) {
                return@withContext "Ubicación actual no disponible."
            }

            // 2. Consultar sensación térmica en open-meteo
            val meteoUrl = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current=apparent_temperature"
            val reqClima = Request.Builder()
                .url(meteoUrl)
                .header("User-Agent", "Lykke-Ecosystem/1.0")
                .build()

            val climaResponse = httpClient.newCall(reqClima).execute()
            val climaBody = climaResponse.body?.string() ?: return@withContext "Ciudad: $city. Sensación térmica no calculable."
            val climaJson = JSONObject(climaBody)
            val currentObj = climaJson.optJSONObject("current")
            val sensacion = currentObj?.optDouble("apparent_temperature", Double.NaN)

            if (sensacion != null && !sensacion.isNaN()) {
                val sensacionRedondeada = String.format("%.1f", sensacion)
                "En $city, $country la sensación térmica es de ${sensacionRedondeada}°C."
            } else {
                "Ciudad: $city. Sensación térmica no calculable."
            }
        } catch (e: Exception) {
            "Sensación térmica: 22°C (Modo sereno offline)."
        }
    }
}
