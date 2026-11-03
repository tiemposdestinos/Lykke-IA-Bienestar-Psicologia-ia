package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.MusicOff
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Palette
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
import com.example.data.model.TextColorChoice
import com.example.data.model.UserProfile
import com.example.ui.NavigationScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LykkeTopBar(
    sessionCount: Int,
    userLevel: Int,
    userXp: Int,
    isMusicPlaying: Boolean,
    textColor: TextColorChoice,
    userProfile: UserProfile?,
    languageCode: String = "es",
    apodoConciencia: String = "Conciencia",
    modoConexion: ModoConexionIA = ModoConexionIA.LOCAL_OFFLINE,
    onOpenSelectorModo: () -> Unit = {},
    onToggleMusic: () -> Unit,
    onNavigate: (NavigationScreen) -> Unit,
    onTriggerSelfCare: () -> Unit,
    onShowContact: () -> Unit,
    onDonationClick: (() -> Unit)? = null,
    onSignOut: () -> Unit
) {
    var showAccountDialog by remember { mutableStateOf(false) }
    val stringsUI = remember(languageCode) { com.example.data.model.Localizer.get(languageCode) }

    val musicIconTint by animateColorAsState(
        targetValue = if (isMusicPlaying) Color(0xFF81C784) else textColor.color.copy(alpha = 0.7f),
        label = "music_tint"
    )

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF0C1924).copy(alpha = 0.88f),
            titleContentColor = textColor.color
        ),
        title = {
            Column(modifier = Modifier.clickable { onNavigate(NavigationScreen.VOICE_CONFIG) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Asesorías y Consultorías Lykke Psicología",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = textColor.color,
                        maxLines = 1
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Entidad: $apodoConciencia",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFFD54F)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${stringsUI.tituloEspacio}",
                        fontSize = 10.5.sp,
                        color = textColor.color.copy(alpha = 0.75f),
                        maxLines = 1
                    )
                }
            }
        },
        actions = {
            // Botón selector de Modo Local vs Internet
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (modoConexion == ModoConexionIA.LOCAL_OFFLINE) Color(0xFF065F46) else Color(0xFF92400E),
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onOpenSelectorModo() }
                    .testTag("topbar_mode_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (modoConexion == ModoConexionIA.LOCAL_OFFLINE) "🛡️ Local" else "🌐 Red",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
            // Botón directo para configurar Voz y Bautizo de Entidad / Mascota
            IconButton(
                onClick = { onNavigate(NavigationScreen.VOICE_CONFIG) },
                modifier = Modifier.testTag("topbar_voice_config_button")
            ) {
                Icon(
                    imageVector = Icons.Default.RecordVoiceOver,
                    contentDescription = "Configurar Voz & Bautizo de Entidad",
                    tint = Color(0xFF80DEEA)
                )
            }
            // Botón directo para cambiar Idiomas en cualquier pantalla
            IconButton(
                onClick = { onNavigate(NavigationScreen.LANGUAGES) },
                modifier = Modifier.testTag("topbar_languages_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Translate,
                    contentDescription = stringsUI.botonIdiomas,
                    tint = Color(0xFFFFD54F)
                )
            }
            // Level & XP gamification badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF004D40).copy(alpha = 0.6f),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onNavigate(NavigationScreen.SESSIONS_DIPLOMA) }
                    .testTag("gamification_badge")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⭐ Nv.$userLevel",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD54F)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${userXp}xp",
                        fontSize = 10.sp,
                        color = Color(0xFF80CBC4)
                    )
                }
            }

            // Piano & Water Ambient Music Toggle Button
            IconButton(
                onClick = onToggleMusic,
                modifier = Modifier.testTag("toggle_ambient_music_btn")
            ) {
                Icon(
                    imageVector = if (isMusicPlaying) Icons.Outlined.MusicNote else Icons.Outlined.MusicOff,
                    contentDescription = "Música de Piano y Agua de Naturaleza",
                    tint = musicIconTint
                )
            }

            // Self-care tip trigger
            IconButton(
                onClick = onTriggerSelfCare,
                modifier = Modifier.testTag("trigger_self_care_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.NotificationsActive,
                    contentDescription = "Recordatorio de Autocuidado",
                    tint = Color(0xFFFFCA28)
                )
            }

            // Environment / Customization button
            IconButton(
                onClick = { onNavigate(NavigationScreen.SETTINGS) },
                modifier = Modifier.testTag("open_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Palette,
                    contentDescription = "Personalizar Entorno",
                    tint = textColor.color
                )
            }

            // User Account Avatar / Sign In button
            IconButton(
                onClick = {
                    if (userProfile != null) {
                        showAccountDialog = true
                    } else {
                        onNavigate(NavigationScreen.AUTH)
                    }
                },
                modifier = Modifier.testTag("user_account_button")
            ) {
                if (userProfile != null) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00897B))
                            .border(1.dp, Color(0xFF80CBC4), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userProfile.displayName.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = "Iniciar Sesión Segura",
                        tint = Color(0xFF80CBC4)
                    )
                }
            }

            // Contact / Brand info button
            IconButton(
                onClick = onShowContact,
                modifier = Modifier.testTag("contact_info_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Contacto Asesorías Consultorias",
                    tint = textColor.color.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.width(2.dp))

            // Botón de Donaciones en Forma de Cápsula en la Esquina Superior Derecha (Color Anaranjado)
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = Color(0xFFF97316), // Naranja cálido y llamativo
                border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFFFEDD5)),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .padding(end = 4.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .clickable {
                        onDonationClick?.invoke() ?: onNavigate(NavigationScreen.SOLIDARITY)
                    }
                    .testTag("btn_donaciones_capsula_topbar")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Donar",
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Donar",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    )

    // Account Profile Dialog
    if (showAccountDialog && userProfile != null) {
        AlertDialog(
            onDismissRequest = { showAccountDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF80CBC4))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mi Cuenta Segura",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = userProfile.displayName,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = userProfile.email,
                        fontSize = 13.sp,
                        color = Color(0xFF80DEEA)
                    )
                    HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Supervisión clínica: ${userProfile.adminNotificationEmail}",
                            fontSize = 12.sp,
                            color = Color(0xFFA5D6A7)
                        )
                    }
                    Text(
                        text = "• Registro ágil: Sin requerir edad ni género.\n• Tus notas del Diario Emocional se mantienen encriptadas.\n• Estado: ${userProfile.syncStatus}",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 17.sp
                    )
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            showAccountDialog = false
                            onNavigate(NavigationScreen.AUTH)
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF80DEEA))
                    ) {
                        Text("Gestionar")
                    }
                    Button(
                        onClick = { showAccountDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
                    ) {
                        Text("Cerrar", color = Color.White)
                    }
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showAccountDialog = false
                        onSignOut()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF8A80))
                ) {
                    Text("Cerrar Sesión")
                }
            },
            containerColor = Color(0xFF102838),
            shape = RoundedCornerShape(18.dp)
        )
    }
}
