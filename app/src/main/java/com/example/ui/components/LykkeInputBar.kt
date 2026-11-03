package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/**
 * Barra de entrada de texto e interacción para chats de Lykke.
 * Incluye botón de adjuntar/opciones en naranja y botón de envío en azul Lykke.
 */
@Composable
fun LykkeInputBar(
    onMessageSent: (String) -> Unit,
    onAddClicked: () -> Unit = {},
    placeholderText: String = "Escribe un mensaje anónimo...",
    modifier: Modifier = Modifier
) {
    var textState by remember { mutableStateOf("") }

    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 8.dp,
        color = Color(0xFF0F1E36)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón de adjuntos/opciones adicionales con el color naranja de la marca
            IconButton(
                onClick = onAddClicked,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFFF7B1B).copy(alpha = 0.2f), shape = CircleShape)
                    .testTag("btn_lykke_input_add")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Opciones",
                    tint = Color(0xFFFF7B1B)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Campo de entrada de texto con bordes redondeados y limpios
            OutlinedTextField(
                value = textState,
                onValueChange = { textState = it },
                placeholder = { Text(placeholderText, color = Color(0xFF94A3B8)) },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(max = 100.dp)
                    .testTag("input_lykke_text"),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0081C9), // Azul Lykke
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedContainerColor = Color(0xFF0A1526),
                    unfocusedContainerColor = Color(0xFF0A1526),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                maxLines = 4
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Botón de envío que se activa solo si hay texto escrito
            IconButton(
                onClick = {
                    if (textState.isNotBlank()) {
                        onMessageSent(textState.trim())
                        textState = ""
                    }
                },
                enabled = textState.isNotBlank(),
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (textState.isNotBlank()) Color(0xFF0081C9) else Color.Gray.copy(alpha = 0.3f),
                        shape = CircleShape
                    )
                    .testTag("btn_lykke_input_send")
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Enviar",
                    tint = Color.White
                )
            }
        }
    }
}
