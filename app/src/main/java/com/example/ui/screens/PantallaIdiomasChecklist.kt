package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MemoriaAprendizajeStore
import com.example.data.model.*
import com.example.ui.components.LykkeSynapseAnimation
import com.example.ui.theme.AzulPlatinadoBg
import com.example.ui.theme.AzulPlatinadoBorder
import com.example.ui.theme.AzulPlatinadoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaIdiomasChecklist(
    currentLanguageCode: String = "es",
    onSelectLanguage: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onStartVoiceInput: ((String) -> Unit) -> Unit = {}
) {
    val context = LocalContext.current
    val memoriaStore = remember { MemoriaAprendizajeStore.getInstance(context) }
    val frasesAprendidas by memoriaStore.ensenanzasFlow.collectAsState()

    var expandedDropdown by remember { mutableStateOf(false) }
    var codigoIdiomaSeleccionado by remember { mutableStateOf(currentLanguageCode) }
    var elementoActualIndex by remember { mutableIntStateOf(0) }
    var textoBusqueda by remember { mutableStateOf("") }
    var mensajeCopiado by remember { mutableStateOf(false) }

    val clipboardManager = LocalClipboardManager.current

    // Sincronizar si cambia externamente
    LaunchedEffect(currentLanguageCode) {
        codigoIdiomaSeleccionado = currentLanguageCode
    }

    // Cargar Strings globales de la UI para el idioma activo
    val stringsUI = remember(codigoIdiomaSeleccionado) {
        Localizer.get(codigoIdiomaSeleccionado)
    }

    // Frases destacadas para explorar
    val frasesDestacadas: List<ElementoFilosofico> = remember {
        bancoDeDatosCompleto.filter { it.id in listOf(101, 102, 103, 4, 5, 6, 7, 601, 602, 603, 604) }
    }

    val elementoActual = frasesDestacadas.getOrElse(elementoActualIndex) { frasesDestacadas.first() }

    // Procesamiento con MotorWellnessLocal / DiccionarioEmocionalLocal y banco ampliado con aprendizaje
    val contenidoTraducido = remember(codigoIdiomaSeleccionado, elementoActualIndex, textoBusqueda, frasesAprendidas) {
        if (textoBusqueda.isNotBlank()) {
            val tagDetectado = MotorWellnessLocal.clasificarEntrada(textoBusqueda)
            if (tagDetectado != null) {
                MotorWellnessLocal.obtenerContenidoUI(tagDetectado, codigoIdiomaSeleccionado)
            } else {
                buscarRespuestaFilosofica(textoBusqueda, codigoIdiomaSeleccionado, frasesAprendidas)
                    ?: elementoActual.traducciones[codigoIdiomaSeleccionado]
                    ?: elementoActual.traducciones["es"]
            }
        } else {
            elementoActual.traducciones[codigoIdiomaSeleccionado]
                ?: elementoActual.traducciones["es"]
        }
    }

    val textoFilosofico = contenidoTraducido?.texto?.ifBlank {
        "Debes continuar en la vida, debes de reinventarte."
    } ?: "Debes continuar en la vida, debes de reinventarte."

    val autorSignatario = contenidoTraducido?.autorSignatario?.ifBlank {
        contenidoTraducido.autor.ifBlank { stringsUI.firmaTarjeta }
    } ?: stringsUI.firmaTarjeta

    val idiomaItemActual = remember(codigoIdiomaSeleccionado) {
        listaIdiomas.find { it.code == codigoIdiomaSeleccionado } ?: listaIdiomas.first()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Barra Superior de Navegación con Nombre Dinámico de la App
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
                    .testTag("btn_back_idiomas")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Regresar al Menú",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringsUI.nombreApp, // Nombre dinámico traducido
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color(0xFFFFD54F)
                )
                Text(
                    text = stringsUI.tituloEspacio,
                    fontSize = 12.sp,
                    color = Color(0xFF38BDF8)
                )
            }

            LykkeSynapseAnimation(
                size = 38.dp,
                showAura = false
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Explicación introductoria
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AzulPlatinadoCard),
            border = BorderStroke(1.dp, AzulPlatinadoBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🌐", fontSize = 26.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Configuración Global de Idioma",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD54F)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Al cambiar de idioma en el checklist, toda la plataforma (menús, botones y títulos) se traduce automáticamente.",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = Color(0xFFCBD5E1)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 🔘 BOTÓN "LANGUAGES" CON CHECKLIST DESPLEGABLE
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = { expandedDropdown = true },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0F382E),
                    contentColor = Color(0xFFFFD54F)
                ),
                border = BorderStroke(1.5.dp, Color(0xFFFFD54F)),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_select_languages")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = null,
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "${stringsUI.botonIdiomas}: ${idiomaItemActual.label}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD54F)
                            )
                            Text(
                                text = "Checklist de 12 Idiomas (Mayas, Danés, Internacionales)",
                                fontSize = 11.sp,
                                color = Color(0xFF86EFAC)
                            )
                        }
                    }
                    Icon(
                        imageVector = if (expandedDropdown) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = "Desplegar Checklist",
                        tint = Color(0xFFFFD54F),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Menú Desplegable con Checklist de Idiomas (12 idiomas completos)
            DropdownMenu(
                expanded = expandedDropdown,
                onDismissRequest = { expandedDropdown = false },
                modifier = Modifier
                    .widthIn(min = 300.dp, max = 360.dp)
                    .heightIn(max = 450.dp)
                    .background(Color(0xFF0F172A))
                    .testTag("dropdown_languages_menu")
            ) {
                Text(
                    text = "CHECKLIST DE IDIOMAS DE LA APP",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38BDF8),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

                listaIdiomas.forEach { idioma ->
                    val isSelected = idioma.code == codigoIdiomaSeleccionado
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = idioma.label,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = if (isSelected) Color(0xFFFFD54F) else Color.White
                                )

                                // Indicador de Selección con tamaño exacto
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Seleccionado",
                                        tint = Color(0xFF4ADE80),
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.RadioButtonUnchecked,
                                        contentDescription = "No seleccionado",
                                        tint = Color(0xFF64748B),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        },
                        onClick = {
                            codigoIdiomaSeleccionado = idioma.code
                            onSelectLanguage(idioma.code) // Actualizar idioma global de la app
                            expandedDropdown = false
                        },
                        modifier = Modifier
                            .background(
                                if (isSelected) Color(0xFF1E293B) else Color.Transparent
                            )
                            .testTag("lang_item_${idioma.code}")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Botón Oficial Corregido: "Club de asesoría ilimitado"
        Button(
            onClick = { /* Acción del club */ },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("btn_club_asesoria_pantalla_idiomas"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C3E2B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringsUI.botonClubAsesoria, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD54F))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Controles de Navegación de Frases
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    elementoActualIndex = if (elementoActualIndex > 0) elementoActualIndex - 1 else frasesDestacadas.size - 1
                    textoBusqueda = ""
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = BorderStroke(1.dp, AzulPlatinadoBorder),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.NavigateBefore, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Anterior", fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = {
                    elementoActualIndex = (elementoActualIndex + 1) % frasesDestacadas.size
                    textoBusqueda = ""
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFD54F)),
                border = BorderStroke(1.dp, Color(0xFFFFD54F)),
                modifier = Modifier.weight(1f)
            ) {
                Text("Siguiente", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.NavigateNext, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }

        // Buscador Inteligente Local (Audio y Texto)
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = textoBusqueda,
            onValueChange = { textoBusqueda = it },
            placeholder = { Text("Escribe o habla (ej. 'estrés', 'sygdom', 'yab'il')...", fontSize = 12.sp, color = Color(0xFF64748B)) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF38BDF8))
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        onStartVoiceInput { transcribed ->
                            textoBusqueda = transcribed
                        }
                    }
                ) {
                    Icon(Icons.Default.Mic, contentDescription = "Dictar por voz", tint = Color(0xFFFFD54F))
                }
            },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = AzulPlatinadoCard,
                unfocusedContainerColor = AzulPlatinadoCard,
                focusedBorderColor = Color(0xFF38BDF8),
                unfocusedBorderColor = AzulPlatinadoBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(40.dp))

        // 🌟 TARJETA DE VISUALIZACIÓN EXACTA
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .testTag("tarjeta_visualizacion_filosofica"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "“",
                    fontSize = 60.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color(0xFF8FA88B),
                    modifier = Modifier.height(40.dp)
                )
                Text(
                    text = textoFilosofico,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF333333),
                    lineHeight = 28.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = autorSignatario,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF666666)
                )

                Spacer(modifier = Modifier.height(18.dp))

                FilledTonalButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString("$textoFilosofico\n$autorSignatario (${idiomaItemActual.label})"))
                        mensajeCopiado = true
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFFE8F5E9),
                        contentColor = Color(0xFF2E7D32)
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = if (mensajeCopiado) Icons.Default.Check else Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (mensajeCopiado) "Copiado" else "Copiar Frase", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}
