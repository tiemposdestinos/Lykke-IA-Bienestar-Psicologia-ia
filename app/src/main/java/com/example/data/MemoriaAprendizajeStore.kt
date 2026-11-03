package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.ContenidoMultilingue
import com.example.data.model.ElementoFilosofico
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

/**
 * Almacén persistente de aprendizaje local para la I.A. Conciencia.
 * Permite que la IA aprenda de las interacciones del usuario, guarde nuevas frases,
 * reflexiones, intenciones y notas en SharedPreferences para que nunca se olviden.
 */
class MemoriaAprendizajeStore(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("lykke_memoria_aprendizaje", Context.MODE_PRIVATE)

    private val _ensenanzasFlow = MutableStateFlow<List<ElementoFilosofico>>(emptyList())
    val ensenanzasFlow: StateFlow<List<ElementoFilosofico>> = _ensenanzasFlow.asStateFlow()

    init {
        cargarMemoria()
    }

    private fun cargarMemoria() {
        val jsonString = prefs.getString("frases_aprendidas", null) ?: return
        try {
            val array = JSONArray(jsonString)
            val lista = mutableListOf<ElementoFilosofico>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val id = obj.optInt("id", 9000 + i)
                val autor = obj.optString("autor", "Usuario & Conciencia")
                val texto = obj.optString("texto", "")
                val titulo = obj.optString("titulo", "Enseñanza Aprendida")
                val tagsJson = obj.optJSONArray("tags")
                val tags = mutableListOf<String>()
                if (tagsJson != null) {
                    for (t in 0 until tagsJson.length()) {
                        tags.add(tagsJson.getString(t))
                    }
                }
                if (tags.isEmpty()) {
                    tags.addAll(texto.lowercase().split(" ").filter { it.length > 3 })
                }

                val traducciones = mapOf(
                    "es" to ContenidoMultilingue(titulo = titulo, texto = texto, autorSignatario = autor, autor = autor),
                    "en" to ContenidoMultilingue(titulo = titulo, texto = texto, autorSignatario = autor, autor = autor),
                    "cak" to ContenidoMultilingue(titulo = titulo, texto = texto, autorSignatario = autor, autor = autor),
                    "quc" to ContenidoMultilingue(titulo = titulo, texto = texto, autorSignatario = autor, autor = autor)
                )

                lista.add(
                    ElementoFilosofico(
                        id = id,
                        tipo = "APRENDIDO",
                        autorOrigen = autor,
                        tags = tags,
                        traducciones = traducciones
                    )
                )
            }
            _ensenanzasFlow.value = lista
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Guarda una nueva enseñanza aprendida en la base de datos persistente.
     */
    fun aprenderNuevaFrase(texto: String, autor: String = "Tú (Enseñanza del Usuario)", titulo: String = "Sabiduría Aprendida"): ElementoFilosofico {
        val actual = _ensenanzasFlow.value.toMutableList()
        val nuevoId = 9000 + actual.size + 1
        val tags = texto.lowercase()
            .replace(Regex("[.,/#!$%^&*;:{}=\\-_`~()?¿¡!]"), "")
            .split(" ")
            .filter { it.length > 2 }

        val traducciones = mapOf(
            "es" to ContenidoMultilingue(titulo = titulo, texto = texto, autorSignatario = autor, autor = autor),
            "en" to ContenidoMultilingue(titulo = titulo, texto = texto, autorSignatario = autor, autor = autor),
            "cak" to ContenidoMultilingue(titulo = titulo, texto = texto, autorSignatario = autor, autor = autor),
            "quc" to ContenidoMultilingue(titulo = titulo, texto = texto, autorSignatario = autor, autor = autor)
        )

        val nuevoElemento = ElementoFilosofico(
            id = nuevoId,
            tipo = "APRENDIDO",
            autorOrigen = autor,
            tags = tags + listOf("aprendido", "ensenanza", "memoria"),
            traducciones = traducciones
        )

        actual.add(0, nuevoElemento)
        _ensenanzasFlow.value = actual

        // Persistir en JSON
        val array = JSONArray()
        for (item in actual) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("autor", item.autorOrigen)
                put("texto", item.traducciones["es"]?.texto ?: "")
                put("titulo", item.traducciones["es"]?.titulo ?: "")
                val tagsArr = JSONArray()
                item.tags.forEach { tagsArr.put(it) }
                put("tags", tagsArr)
            }
            array.put(obj)
        }
        prefs.edit().putString("frases_aprendidas", array.toString()).apply()

        return nuevoElemento
    }

    fun obtenerTodasLasAprendidas(): List<ElementoFilosofico> = _ensenanzasFlow.value

    companion object {
        @Volatile
        private var instance: MemoriaAprendizajeStore? = null

        fun getInstance(context: Context): MemoriaAprendizajeStore {
            return instance ?: synchronized(this) {
                instance ?: MemoriaAprendizajeStore(context.applicationContext).also { instance = it }
            }
        }
    }
}
