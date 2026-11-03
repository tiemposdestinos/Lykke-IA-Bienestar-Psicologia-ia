package com.example.data.model

import java.net.URLEncoder

enum class ServiceType { BUSQUEDA, MAPAS, NUBE }

data class GlobalService(
    val id: String,
    val name: String,
    val type: ServiceType,
    val region: String,
    val flagEmoji: String,
    val targetUrl: String,
    val descripcion: String
) {
    /**
     * Construye la URL de navegación con el término de búsqueda ingresado,
     * activando la vista satelital explícita para servicios de mapas.
     */
    fun buildUrlWithQuery(query: String = ""): String {
        if (query.isBlank()) {
            return when (id) {
                // Mapas con vista satelital por defecto
                "2" -> "https://maps.google.com/maps?t=k"
                "9" -> "https://yandex.com/maps/?l=sat"
                else -> targetUrl
            }
        }

        val encoded = try {
            URLEncoder.encode(query.trim(), "UTF-8")
        } catch (e: Exception) {
            query.trim().replace(" ", "+")
        }

        return when (id) {
            "1" -> "https://www.google.com/search?q=$encoded"
            "2" -> "https://maps.google.com/maps?q=$encoded&t=k" // Capa Satélite Google Maps fotográfica
            "3" -> "https://duckduckgo.com/?q=$encoded"
            "4" -> "https://www.ecosia.org/search?q=$encoded"
            "5" -> "https://www.baidu.com/s?wd=$encoded"
            "6" -> "https://map.baidu.com/?newmap=1&s=s%26wd%3D$encoded" // Baidu Maps
            "7" -> "https://www.amap.com/search?query=$encoded" // Gaode Maps (Amap)
            "8" -> "https://ya.ru/search/?text=$encoded" // Yandex Search
            "9" -> "https://yandex.com/maps/?text=$encoded&l=sat" // Yandex Satélite
            "10" -> "https://2gis.ru/search/$encoded" // 2GIS Maps
            "12" -> "https://www.qwant.com/?q=$encoded" // Qwant
            else -> if (type == ServiceType.MAPAS) {
                "https://maps.google.com/maps?q=$encoded&t=k"
            } else {
                "https://www.google.com/search?q=$encoded"
            }
        }
    }
}

object GlobalNetworkRegistry {
    fun getServiceById(id: String): GlobalService? = services.find { it.id == id }
    val googleSearch: GlobalService get() = services.first { it.id == "1" }
    val googleMapsSat: GlobalService get() = services.first { it.id == "2" }
    val duckDuckGo: GlobalService get() = services.first { it.id == "3" }
    val baiduSearch: GlobalService get() = services.first { it.id == "5" }
    val baiduMaps: GlobalService get() = services.first { it.id == "6" }
    val gaodeMaps: GlobalService get() = services.first { it.id == "7" }
    val yandexSearch: GlobalService get() = services.first { it.id == "8" }
    val yandexMapsSat: GlobalService get() = services.first { it.id == "9" }

    val services = listOf(
        // Mercado Global / Occidental
        GlobalService(
            id = "1",
            name = "Google Search",
            type = ServiceType.BUSQUEDA,
            region = "Global",
            flagEmoji = "🌐",
            targetUrl = "https://www.google.com",
            descripcion = "Motor de búsqueda universal y servidores mundiales"
        ),
        GlobalService(
            id = "2",
            name = "Google Maps (Satélite)",
            type = ServiceType.MAPAS,
            region = "Global",
            flagEmoji = "🛰️",
            targetUrl = "https://www.google.com/maps/@0,0,3z/data=!3m1!1e3?force=canvas&t=k",
            descripcion = "Fotografía satelital en alta resolución, relieve y exploración global"
        ),
        GlobalService(
            id = "3",
            name = "DuckDuckGo",
            type = ServiceType.BUSQUEDA,
            region = "Privacidad Global",
            flagEmoji = "🦆",
            targetUrl = "https://duckduckgo.com",
            descripcion = "Búsqueda privada sin rastreo ni perfilado de usuario"
        ),
        GlobalService(
            id = "4",
            name = "Ecosia",
            type = ServiceType.BUSQUEDA,
            region = "Ecológico Global",
            flagEmoji = "🌱",
            targetUrl = "https://www.ecosia.org",
            descripcion = "Búsquedas ecológicas que plantan árboles con cada consulta"
        ),
        
        // Mercado Chino (Asia)
        GlobalService(
            id = "5",
            name = "Baidu Search",
            type = ServiceType.BUSQUEDA,
            region = "China / Asia",
            flagEmoji = "🇨🇳",
            targetUrl = "https://www.baidu.com",
            descripcion = "Motor de búsqueda líder en Asia con servidores en China"
        ),
        GlobalService(
            id = "6",
            name = "Baidu Maps",
            type = ServiceType.MAPAS,
            region = "China / Asia",
            flagEmoji = "🇨🇳",
            targetUrl = "https://map.baidu.com",
            descripcion = "Cartografía satelital y navegación urbana en Asia"
        ),
        GlobalService(
            id = "7",
            name = "Gaode Maps (Amap)",
            type = ServiceType.MAPAS,
            region = "China / Asia",
            flagEmoji = "🇨🇳",
            targetUrl = "https://www.amap.com",
            descripcion = "Mapas satelitales de alta precisión de Alibaba Gaode"
        ),
        
        // Mercado Ruso / Euroasiático
        GlobalService(
            id = "8",
            name = "Yandex Search",
            type = ServiceType.BUSQUEDA,
            region = "Rusia / CIS",
            flagEmoji = "🇷🇺",
            targetUrl = "https://ya.ru",
            descripcion = "Ecosistema de búsqueda inteligente de Europa del Este"
        ),
        GlobalService(
            id = "9",
            name = "Yandex Maps (Satélite)",
            type = ServiceType.MAPAS,
            region = "Rusia / CIS",
            flagEmoji = "🛰️",
            targetUrl = "https://yandex.com/maps/?l=sat",
            descripcion = "Cartografía satelital detallada para Eurasia y el mundo"
        ),
        GlobalService(
            id = "10",
            name = "2GIS Maps",
            type = ServiceType.MAPAS,
            region = "Rusia / CIS",
            flagEmoji = "🇷🇺",
            targetUrl = "https://2gis.ru",
            descripcion = "Directorio urbano detallado y modelos 3D de 2GIS"
        ),
        GlobalService(
            id = "11",
            name = "VK Cloud",
            type = ServiceType.NUBE,
            region = "Rusia / CIS",
            flagEmoji = "🇷🇺",
            targetUrl = "https://vk.cloud",
            descripcion = "Infraestructura en la nube y servidores de datos VK"
        ),
        
        // Mercado Europeo Específico
        GlobalService(
            id = "12",
            name = "Qwant",
            type = ServiceType.BUSQUEDA,
            region = "Francia / UE",
            flagEmoji = "🇫🇷",
            targetUrl = "https://www.qwant.com",
            descripcion = "Motor de búsqueda europeo que respeta tu privacidad"
        ),
        GlobalService(
            id = "13",
            name = "DeIC Network",
            type = ServiceType.NUBE,
            region = "Dinamarca / Nórdico",
            flagEmoji = "🇩🇰",
            targetUrl = "https://www.deic.dk",
            descripcion = "Red nacional danesa de investigación y e-infraestructura"
        )
    )
}
