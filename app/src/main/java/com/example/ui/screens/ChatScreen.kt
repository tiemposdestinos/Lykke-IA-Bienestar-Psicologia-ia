package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.NotificationsActive
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
import com.example.data.model.ChatMessage
import com.example.data.model.TextColorChoice
import com.example.ui.NavigationScreen
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatScreen(
    messages: List<ChatMessage>,
    isTyping: Boolean,
    textColor: TextColorChoice,
    onSendMessage: (String) -> Unit,
    onTriggerSelfCare: () -> Unit,
    onNavigate: (NavigationScreen) -> Unit,
    onStartVoiceInput: ((String) -> Unit) -> Unit = {}
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(messages.size, isTyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        // Safe Environment & Confidentiality Banner
        Surface(
            color = Color(0xFF003830).copy(alpha = 0.90f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = "Confidencial",
                    tint = Color(0xFF80CBC4),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Entorno Seguro y Confidencial: Cuéntanos tu historia sin juicios.",
                    fontSize = 11.sp,
                    color = Color(0xFFE0F2F1),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "LivsForm Lykke",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF80CBC4)
                )
            }
        }

        // Quick Venting & Self-care prompt chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0A1822).copy(alpha = 0.85f))
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AssistChip(
                onClick = { onSendMessage("Hoy siento un nivel muy alto de estrés y agotamiento mental.") },
                label = { Text("Tengo mucho estrés hoy", fontSize = 11.sp) },
                leadingIcon = { Icon(Icons.Default.Spa, contentDescription = null, modifier = Modifier.size(14.dp)) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = Color(0xFF152A38),
                    labelColor = Color(0xFF80DEEA)
                )
            )

            AssistChip(
                onClick = { onSendMessage("Siento mucha frustración porque las cosas no salen como planeé.") },
                label = { Text("Manejo de frustración", fontSize = 11.sp) },
                leadingIcon = { Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(14.dp)) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = Color(0xFF152A38),
                    labelColor = Color(0xFFFFAB91)
                )
            )

            AssistChip(
                onClick = { onSendMessage("Tengo metas y sueños pero el miedo al fracaso me hace postergar.") },
                label = { Text("Miedo en mis metas", fontSize = 11.sp) },
                leadingIcon = { Icon(Icons.Default.EmojiEvents, contentDescription = null, modifier = Modifier.size(14.dp)) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = Color(0xFF152A38),
                    labelColor = Color(0xFFFFE082)
                )
            )

            AssistChip(
                onClick = onTriggerSelfCare,
                label = { Text("Recordatorio de equilibrio", fontSize = 11.sp) },
                leadingIcon = { Icon(Icons.Outlined.NotificationsActive, contentDescription = null, modifier = Modifier.size(14.dp)) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = Color(0xFF1E293B),
                    labelColor = Color(0xFF38BDF8)
                )
            )

            AssistChip(
                onClick = { onNavigate(NavigationScreen.BREATHING) },
                label = { Text("Ejercicio de respiración", fontSize = 11.sp) },
                leadingIcon = { Icon(Icons.Default.Air, contentDescription = null, modifier = Modifier.size(14.dp)) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = Color(0xFF152A38),
                    labelColor = Color(0xFF81D4FA)
                )
            )
        }

        // Message List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatBubble(
                    message = msg,
                    textColor = textColor,
                    onNavigateToBooking = { onNavigate(NavigationScreen.BOOKING) },
                    onStartBreathing = { onNavigate(NavigationScreen.BREATHING) }
                )
            }

            if (isTyping) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = Color(0xFF80CBC4)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Lykke Bot está procesando con empatía...",
                            fontSize = 12.sp,
                            color = Color(0xFF80CBC4)
                        )
                    }
                }
            }
        }

        // Input Field Bar
        Surface(
            color = Color(0xFF0C1924).copy(alpha = 0.95f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            "Escribe tu historia o desahógate aquí...",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF26A69A),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF13222F),
                        unfocusedContainerColor = Color(0xFF13222F)
                    ),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = {
                        onStartVoiceInput { spokenText ->
                            if (spokenText.isNotBlank()) {
                                inputText = if (inputText.isBlank()) spokenText else "$inputText $spokenText"
                            }
                        }
                    },
                    modifier = Modifier.testTag("chat_voice_mic_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Escuchar por voz (Audio)",
                        tint = Color(0xFFFFD54F)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                FloatingActionButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            val textToSend = inputText
                            inputText = ""
                            onSendMessage(textToSend)
                            coroutineScope.launch {
                                listState.animateScrollToItem(messages.size)
                            }
                        }
                    },
                    shape = CircleShape,
                    containerColor = Color(0xFF00897B),
                    contentColor = Color.White,
                    modifier = Modifier
                        .size(46.dp)
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Enviar desahogo",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    textColor: TextColorChoice,
    onNavigateToBooking: () -> Unit,
    onStartBreathing: () -> Unit
) {
    val isUser = message.isFromUser
    val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp))

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isUser) 18.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 18.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    isUser -> Color(0xFF00695C)
                    message.isSpecialNotification -> Color(0xFF2E4C38).copy(alpha = 0.95f)
                    else -> Color(0xFF142736).copy(alpha = 0.95f)
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (!isUser) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF80CBC4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (message.isSpecialNotification) Icons.Default.Notifications else Icons.Default.SelfImprovement,
                                contentDescription = null,
                                tint = Color(0xFF003830),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (message.isSpecialNotification) "Recordatorio de Equilibrio" else "Lykke Bot (Psicología)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF80CBC4)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Text(
                    text = message.text,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = if (isUser) Color.White else textColor.color
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = timeStr,
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.55f),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
