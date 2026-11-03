package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material.icons.outlined.PhoneInTalk
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookingRequest
import com.example.data.model.TextColorChoice

@Composable
fun BookingScreen(
    bookings: List<BookingRequest>,
    textColor: TextColorChoice,
    onBookAppointment: (
        name: String,
        whatsapp: String,
        email: String,
        sessionType: String,
        motive: String,
        datePref: String,
        timeSlot: String
    ) -> Boolean
) {
    var selectedType by remember { mutableStateOf("video") } // "audio" ($5) or "video" ($15)

    var clientName by remember { mutableStateOf("") }
    var whatsappNumber by remember { mutableStateOf("") }
    var emailAddress by remember { mutableStateOf("") }
    var motiveText by remember { mutableStateOf("") }
    var datePreference by remember { mutableStateOf("Mañana (o fecha más próxima)") }
    var selectedTimeSlot by remember { mutableStateOf("Tarde (15:00 - 18:00)") }

    val timeSlots = listOf(
        "Mañana (09:00 - 12:00)",
        "Mediodía (12:00 - 15:00)",
        "Tarde (15:00 - 18:00)",
        "Noche (18:00 - 21:00)"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Asesoría Profesional LivsForm Lykke",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor.color
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Agenda una sesión directa con nuestros psicólogos profesionales para orientación profunda sobre estrés, frustración o metas.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = textColor.color.copy(alpha = 0.85f)
                )
            }
        }

        // Service Type Selector Tabs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Audio session card ($5)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedType == "audio") Color(0xFF004D40) else Color(0xFF132330).copy(alpha = 0.85f)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            width = if (selectedType == "audio") 2.dp else 1.dp,
                            color = if (selectedType == "audio") Color(0xFF80CBC4) else Color.White.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .testTag("tab_audio_session")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PhoneInTalk,
                                contentDescription = null,
                                tint = Color(0xFF80CBC4),
                                modifier = Modifier.size(22.dp)
                            )
                            Badge(containerColor = Color(0xFF00796B)) {
                                Text("$5 USD", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Sesión Audio",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Psicólogo Digital guiado",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        RadioButton(
                            selected = selectedType == "audio",
                            onClick = { selectedType = "audio" },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF80CBC4))
                        )
                    }
                }

                // Video call session card ($15)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedType == "video") Color(0xFF004D40) else Color(0xFF132330).copy(alpha = 0.85f)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            width = if (selectedType == "video") 2.dp else 1.dp,
                            color = if (selectedType == "video") Color(0xFF80CBC4) else Color.White.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .testTag("tab_video_session")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Videocam,
                                contentDescription = null,
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(24.dp)
                            )
                            Badge(containerColor = Color(0xFFE65100)) {
                                Text("$15 USD", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Videollamada",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Cita con Profesional",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        RadioButton(
                            selected = selectedType == "video",
                            onClick = { selectedType = "video" },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFFD54F))
                        )
                    }
                }
            }
        }

        // Form Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF102636).copy(alpha = 0.92f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("booking_form_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (selectedType == "video")
                            "Formulario de Agendamiento de Videollamada ($15 USD)"
                        else
                            "Formulario de Sesión Audio Psicólogo Digital ($5 USD)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedType == "video") Color(0xFFFFD54F) else Color(0xFF80CBC4)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Deja tus datos para coordinar el horario y el inicio de tu sesión:",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = clientName,
                        onValueChange = { clientName = it },
                        label = { Text("Nombre Completo") },
                        placeholder = { Text("Ej. María Fernández") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("booking_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF80CBC4),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = whatsappNumber,
                        onValueChange = { whatsappNumber = it },
                        label = { Text("Número de WhatsApp (con código de país)") },
                        placeholder = { Text("Ej. +52 55 1234 5678") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("booking_whatsapp_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF80CBC4),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = emailAddress,
                        onValueChange = { emailAddress = it },
                        label = { Text("Correo Electrónico") },
                        placeholder = { Text("tuemail@gmail.com") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("booking_email_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF80CBC4),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = motiveText,
                        onValueChange = { motiveText = it },
                        label = { Text("Motivo de Consulta") },
                        placeholder = { Text("Ej. Manejo de estrés laboral, frustración con metas o proyectos...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("booking_motive_input"),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF80CBC4),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Selección de Horario Preferido:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        timeSlots.forEach { slot ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selectedTimeSlot == slot) Color(0xFF004D40).copy(alpha = 0.5f) else Color.Transparent)
                                    .padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedTimeSlot == slot,
                                    onClick = { selectedTimeSlot = slot },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF80CBC4))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = slot, fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }

                    // Notice: "Solo queda pendiente la cuenta"
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF332A0F).copy(alpha = 0.8f))
                            .border(1.dp, Color(0xFFFFD54F).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Nota del Proceso: Solo queda pendiente la cuenta.",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F)
                                )
                                Text(
                                    text = "Al enviar el formulario, tu cita queda 'En espera de confirmación para inicio de sesión'. Recibirás por WhatsApp y correo los datos bancarios/pasarela para confirmar tu horario.",
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val typeLabel = if (selectedType == "video")
                                "Psicología en Videollamada ($15 USD)"
                            else
                                "Sesión Audio Psicólogo Digital ($5 USD)"

                            val success = onBookAppointment(
                                clientName,
                                whatsappNumber,
                                emailAddress,
                                typeLabel,
                                motiveText,
                                datePreference,
                                selectedTimeSlot
                            )
                            if (success) {
                                motiveText = ""
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedType == "video") Color(0xFFFF8F00) else Color(0xFF00796B)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_booking_btn")
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedType == "video") "Programar Cita Videollamada ($15)" else "Solicitar Sesión Audio ($5)",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Active bookings history list
        if (bookings.isNotEmpty()) {
            item {
                Text(
                    text = "Tus Citas Programadas",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor.color
                )
            }

            items(bookings, key = { it.id }) { booking ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF142738).copy(alpha = 0.95f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = booking.sessionType,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFFFFD54F)
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFE65100).copy(alpha = 0.35f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.HourglassTop,
                                        contentDescription = null,
                                        tint = Color(0xFFFFB74D),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "En espera de confirmación",
                                        fontSize = 10.sp,
                                        color = Color(0xFFFFB74D),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Paciente: ${booking.clientName} | WhatsApp: ${booking.whatsapp}",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Correo: ${booking.email} | Horario: ${booking.timeSlot}",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Motivo: ${booking.motive}",
                            fontSize = 12.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = Color(0xFF80CBC4)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "⚠️ ${booking.paymentNote}: recibirás el número de cuenta para completar el pago y activar el enlace de videollamada.",
                            fontSize = 11.sp,
                            color = Color(0xFFFFD54F).copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }
    }
}
