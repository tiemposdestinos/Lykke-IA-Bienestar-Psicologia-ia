package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DiaryBackupReport
import com.example.data.model.DiaryEntry
import com.example.data.model.TextColorChoice
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DiaryScreen(
    entries: List<DiaryEntry>,
    backupReports: List<DiaryBackupReport>,
    textColor: TextColorChoice,
    onSaveEntry: (title: String, content: String, tags: List<String>, mood: String, authorAlias: String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTagFilter by remember { mutableStateOf("Todas") }

    var isWritingNewEntry by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newContent by remember { mutableStateOf("") }
    var authorAlias by remember { mutableStateOf("UsuarioAnónimo") }
    var selectedMood by remember { mutableStateOf("🌱") }
    var selectedTags by remember { mutableStateOf(setOf("ansiedad")) }

    val availableTags = listOf("ansiedad", "alegría", "frustración", "estrés", "metas", "esperanza", "calma")
    val moodList = listOf("🌱", "🌊", "⚡", "🔥", "☀️", "🕊️")

    val filteredEntries = entries.filter { entry ->
        val matchesSearch = searchQuery.isBlank() ||
                entry.title.contains(searchQuery, ignoreCase = true) ||
                entry.plainContent.contains(searchQuery, ignoreCase = true)
        val matchesTag = selectedTagFilter == "Todas" || entry.tags.contains(selectedTagFilter)
        matchesSearch && matchesTag
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Privacy Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F302A).copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("diary_privacy_banner")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF80CBC4).copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Lock, contentDescription = null, tint = Color(0xFF80CBC4))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Diario Emocional Seguro",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Entorno Encriptado, Anónimo & Confidencial",
                                fontSize = 12.sp,
                                color = Color(0xFF80CBC4)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Escribe tus pensamientos y desahógate con total libertad. Cada relato se encripta localmente para que solo tú tengas acceso.",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    // 5 stories backup info
                    val currentCount = entries.size
                    val progressToFive = currentCount % 5
                    val neededForFive = if (progressToFive == 0 && currentCount > 0) 5 else 5 - progressToFive

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF004D40).copy(alpha = 0.5f))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Security, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Respaldo y Supervisión de 5 Relatos:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F)
                                )
                                Text(
                                    text = "Por cada 5 relatos, se emite una copia confidencial a tiemposdestinos@gmail.com con tu seudónimo y conexión segura. Llevas $currentCount relatos (faltan $neededForFive para el siguiente reporte).",
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Toggle / New entry creator
        item {
            if (!isWritingNewEntry) {
                Button(
                    onClick = { isWritingNewEntry = true },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("open_write_diary_btn")
                ) {
                    Icon(Icons.Default.EditNote, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Escribir Nuevo Relato Seguro (+40 XP)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            } else {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF102838).copy(alpha = 0.95f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_diary_form_card")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Nuevo Relato Confidencial",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD54F)
                            )
                            IconButton(onClick = { isWritingNewEntry = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = authorAlias,
                                onValueChange = { authorAlias = it },
                                label = { Text("Nombre de Usuario Anónimo") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color(0xFF80CBC4),
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                                )
                            )

                            // Mood selector
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF173549))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    moodList.forEach { m ->
                                        Text(
                                            text = m,
                                            fontSize = 20.sp,
                                            modifier = Modifier
                                                .clickable { selectedMood = m }
                                                .clip(CircleShape)
                                                .background(if (selectedMood == m) Color(0xFF00796B) else Color.Transparent)
                                                .padding(4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = newTitle,
                            onValueChange = { newTitle = it },
                            label = { Text("Título de tu relato (opcional)") },
                            placeholder = { Text("Ej. Mi sentir hoy ante los imprevistos...") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF80CBC4),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Etiquetas emocionales (selecciona una o varias):",
                            fontSize = 12.sp,
                            color = Color(0xFF80CBC4),
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            availableTags.forEach { tag ->
                                val isSelected = selectedTags.contains(tag)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedTags = if (isSelected) {
                                            selectedTags - tag
                                        } else {
                                            selectedTags + tag
                                        }
                                    },
                                    label = { Text(tag, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF00796B),
                                        selectedLabelColor = Color.White,
                                        containerColor = Color(0xFF13222F),
                                        labelColor = Color(0xFFB0BEC5)
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = newContent,
                            onValueChange = { newContent = it },
                            label = { Text("Desahogo o pensamiento íntimo...") },
                            placeholder = { Text("Escribe con sinceridad. Nadie te juzgará aquí...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 120.dp)
                                .testTag("diary_content_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF80CBC4),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                onSaveEntry(
                                    newTitle,
                                    newContent,
                                    selectedTags.toList(),
                                    selectedMood,
                                    authorAlias
                                )
                                newTitle = ""
                                newContent = ""
                                isWritingNewEntry = false
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26A69A)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("save_diary_entry_btn")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, tint = Color(0xFF002922))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Guardar y Encriptar Relato (+40 XP)", fontWeight = FontWeight.Bold, color = Color(0xFF002922))
                        }
                    }
                }
            }
        }

        // Search & Filter Bar
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF122330).copy(alpha = 0.90f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Buscar en relatos pasados...", fontSize = 12.sp, color = Color.White.copy(alpha = 0.5f)) },
                        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = Color(0xFF80CBC4)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Limpiar", tint = Color.White)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("diary_search_field"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF80CBC4),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        (listOf("Todas") + availableTags).forEach { tag ->
                            val isSelected = selectedTagFilter == tag
                            SuggestionChip(
                                onClick = { selectedTagFilter = tag },
                                label = { Text(tag, fontSize = 11.sp) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (isSelected) Color(0xFF00796B) else Color(0xFF1A3344),
                                    labelColor = if (isSelected) Color.White else Color(0xFFB0BEC5)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Entries List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tus Relatos Guardados (${filteredEntries.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor.color
                )
            }
        }

        if (filteredEntries.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF10222E).copy(alpha = 0.85f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.HistoryEdu, contentDescription = null, tint = Color(0xFF80CBC4), modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No hay relatos que coincidan con la búsqueda.",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        items(filteredEntries, key = { it.id }) { entry ->
            DiaryEntryCard(entry = entry)
        }

        // Reports History
        if (backupReports.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Historial de Reportes Emitidos a tiemposdestinos@gmail.com",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD54F)
                )
            }

            items(backupReports, key = { it.batchId }) { report ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.95f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Lote #${report.batchNumber} (${report.entryCount} relatos)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFA5D6A7)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(report.timestamp)),
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Usuario: ${report.authorAlias} | Destino: ${report.destinationEmail}",
                            fontSize = 11.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Red / Origen: ${report.ipNetworkInfo}",
                            fontSize = 10.sp,
                            color = Color(0xFF80CBC4)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DiaryEntryCard(entry: DiaryEntry) {
    var isShowingEncryptedRaw by remember { mutableStateOf(false) }
    val dateStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(entry.timestamp))

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF132535).copy(alpha = 0.92f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = entry.moodEmoji, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = entry.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Por: ${entry.authorAlias} • $dateStr",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.65f)
                        )
                    }
                }

                IconButton(
                    onClick = { isShowingEncryptedRaw = !isShowingEncryptedRaw },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isShowingEncryptedRaw) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = "Ver cifrado",
                        tint = Color(0xFF80CBC4),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tags
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                entry.tags.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF004D40).copy(alpha = 0.6f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = "#$tag", fontSize = 10.sp, color = Color(0xFF80CBC4))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (!isShowingEncryptedRaw) {
                Text(
                    text = entry.plainContent,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = Color.White.copy(alpha = 0.92f)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0A1822))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "🔒 Bloque Cifrado AES-256 (En tránsito & reposo):",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = entry.encryptedContent,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF80DEEA),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}
