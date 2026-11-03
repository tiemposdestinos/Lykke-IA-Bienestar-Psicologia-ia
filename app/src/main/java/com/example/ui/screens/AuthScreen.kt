package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.data.model.UserProfile
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    currentUser: FirebaseUser? = null,
    userProfile: UserProfile? = null,
    onAuthSuccess: (user: FirebaseUser, alias: String?) -> Unit = { _, _ -> },
    onSignOut: () -> Unit = {},
    onUpdateAlias: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var userAliasInput by remember { mutableStateOf("") }
    var isEditingAlias by remember { mutableStateOf(false) }
    var newAliasInput by remember { mutableStateOf("") }
    var showSignOutConfirmDialog by remember { mutableStateOf(false) }

    BackHandler {
        onNavigateBack()
    }

    // Attempt silent auto-sign in ONLY when initially unauthenticated
    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            attemptAutoSignIn(
                context = context,
                credentialManager = credentialManager,
                onAuthSuccess = { user ->
                    onAuthSuccess(user, null)
                },
                onUnauthenticated = { /* remain on unauthenticated view */ },
                scope = scope
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A1926),
                        Color(0xFF0F2B38),
                        Color(0xFF133B49)
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (currentUser != null) "Mi Cuenta Segura" else "Autenticación Segura",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.testTag("auth_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Regresar",
                                tint = Color(0xFF80CBC4)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF0A1926).copy(alpha = 0.85f)
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (currentUser != null) {
                    // =========================================================================
                    // VIEW 1: AUTHENTICATED USER DASHBOARD
                    // =========================================================================
                    AuthenticatedUserCard(
                        currentUser = currentUser,
                        userProfile = userProfile,
                        isEditingAlias = isEditingAlias,
                        newAliasInput = newAliasInput,
                        onToggleEditAlias = {
                            isEditingAlias = !isEditingAlias
                            if (isEditingAlias) {
                                newAliasInput = userProfile?.displayName ?: currentUser.displayName ?: ""
                            }
                        },
                        onNewAliasChange = { newAliasInput = it },
                        onSaveNewAlias = {
                            if (newAliasInput.isNotBlank()) {
                                onUpdateAlias(newAliasInput)
                                isEditingAlias = false
                            }
                        },
                        onContinueToApp = onNavigateBack,
                        onSwitchAccount = {
                            isLoading = true
                            errorMessage = null
                            performSignOut(
                                context = context,
                                credentialManager = credentialManager,
                                onSignOutComplete = {
                                    onSignOut()
                                    onGoogleSignInClicked(
                                        context = context,
                                        credentialManager = credentialManager,
                                        customAlias = null,
                                        onAuthSuccess = { user, alias ->
                                            isLoading = false
                                            onAuthSuccess(user, alias)
                                        },
                                        onAuthError = { err ->
                                            isLoading = false
                                            errorMessage = err
                                        },
                                        scope = scope,
                                        onAuthCancelled = {
                                            isLoading = false
                                        }
                                    )
                                },
                                scope = scope
                            )
                        },
                        onRequestSignOut = { showSignOutConfirmDialog = true },
                        isLoading = isLoading
                    )
                } else {
                    // =========================================================================
                    // VIEW 2: UNAUTHENTICATED GOOGLE SIGN-IN INTERFACE
                    // =========================================================================
                    UnauthenticatedSignInCard(
                        userAlias = userAliasInput,
                        onUserAliasChange = { userAliasInput = it },
                        isLoading = isLoading,
                        errorMessage = errorMessage,
                        onDismissError = { errorMessage = null },
                        onSignInClick = {
                            isLoading = true
                            errorMessage = null
                            onGoogleSignInClicked(
                                context = context,
                                credentialManager = credentialManager,
                                customAlias = userAliasInput.ifBlank { null },
                                onAuthSuccess = { user, alias ->
                                    isLoading = false
                                    onAuthSuccess(user, alias)
                                    onNavigateBack()
                                },
                                onAuthError = { err ->
                                    isLoading = false
                                    errorMessage = err
                                },
                                scope = scope,
                                onAuthCancelled = {
                                    isLoading = false
                                }
                            )
                        },
                        onExploreAsGuest = onNavigateBack
                    )
                }
            }
        }
    }

    // Confirmation dialog before signing out
    if (showSignOutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFFFB74D))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cerrar Sesión Segura", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            text = {
                Text(
                    text = "¿Deseas cerrar tu sesión? Tus notas del Diario Emocional y tus avances seguirán respaldados y protegidos en Firestore bajo la supervisión de tiemposdestinos@gmail.com.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSignOutConfirmDialog = false
                        performSignOut(
                            context = context,
                            credentialManager = credentialManager,
                            onSignOutComplete = onSignOut,
                            scope = scope
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Cerrar Sesión", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showSignOutConfirmDialog = false },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF80CBC4))
                ) {
                    Text("Cancelar")
                }
            },
            containerColor = Color(0xFF132B3C),
            shape = RoundedCornerShape(18.dp)
        )
    }
}

/**
 * Card rendered when the user is authenticated.
 */
@Composable
private fun AuthenticatedUserCard(
    currentUser: FirebaseUser,
    userProfile: UserProfile?,
    isEditingAlias: Boolean,
    newAliasInput: String,
    onToggleEditAlias: () -> Unit,
    onNewAliasChange: (String) -> Unit,
    onSaveNewAlias: () -> Unit,
    onContinueToApp: () -> Unit,
    onSwitchAccount: () -> Unit,
    onRequestSignOut: () -> Unit,
    isLoading: Boolean
) {
    val displayName = userProfile?.displayName?.ifBlank { null }
        ?: currentUser.displayName?.ifBlank { null }
        ?: "Usuario de Psicología"
    val email = currentUser.email ?: "usuario@livsformlykke.com"
    val initial = displayName.take(1).uppercase()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 520.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF132B3C).copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00897B).copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar Circle
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF00796B), Color(0xFF004D40))
                        )
                    )
                    .border(2.5.dp, Color(0xFF80CBC4), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Display Name & Alias Editor
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = displayName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = onToggleEditAlias,
                    modifier = Modifier.size(28.dp).testTag("edit_alias_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Modificar Alias",
                        tint = Color(0xFF80CBC4),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(
                text = email,
                fontSize = 13.sp,
                color = Color(0xFF80DEEA)
            )

            // Inline Alias Editor Field
            AnimatedVisibility(visible = isEditingAlias) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = newAliasInput,
                        onValueChange = onNewAliasChange,
                        label = { Text("Nuevo Nombre o Alias") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("new_alias_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF80CBC4),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color(0xFF80CBC4),
                            unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onSaveNewAlias,
                        modifier = Modifier.fillMaxWidth().testTag("save_alias_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Guardar Alias en la Nube", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sync Status Chip
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF004D40).copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81C784).copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF81C784),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Cuenta Conectada y Protegida",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE8F5E9)
                        )
                        Text(
                            text = "Respaldo activo en Firebase Cloud Firestore",
                            fontSize = 11.sp,
                            color = Color(0xFFA5D6A7)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Clinical Supervision & Frictionless Notice
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0B1F2D), RoundedCornerShape(12.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Supervisión institucional:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFFD54F)
                    )
                }
                Text(
                    text = "tiemposdestinos@gmail.com",
                    fontSize = 12.sp,
                    color = Color(0xFF80DEEA),
                    modifier = Modifier.padding(start = 24.dp)
                )
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 4.dp))
                Text(
                    text = "• Privacidad total: Sin requerir edad ni género.\n• Tu Diario Emocional se almacena con encriptación local y en la nube.\n• Tu avance hacia las 10 sesiones está resguardado.",
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Action: Continue to App
            Button(
                onClick = onContinueToApp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("continue_to_app_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B))
            ) {
                Icon(Icons.Default.Home, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Continuar en la Aplicación", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Secondary: Switch Account
            OutlinedButton(
                onClick = onSwitchAccount,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("switch_google_account_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF80DEEA)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF80CBC4).copy(alpha = 0.6f))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color(0xFF80DEEA))
                } else {
                    Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cambiar de Cuenta de Google", fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sign Out Button
            TextButton(
                onClick = onRequestSignOut,
                modifier = Modifier.testTag("sign_out_button")
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color(0xFFFF8A80), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Cerrar Sesión", color = Color(0xFFFF8A80), fontSize = 13.sp)
            }
        }
    }
}

/**
 * Card rendered when unauthenticated, providing Google Sign-In with Credential Manager.
 */
@Composable
private fun UnauthenticatedSignInCard(
    userAlias: String,
    onUserAliasChange: (String) -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    onDismissError: () -> Unit,
    onSignInClick: () -> Unit,
    onExploreAsGuest: () -> Unit
) {
    // Shield header icon
    Box(
        modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .background(Color(0xFF00796B).copy(alpha = 0.25f))
            .border(2.dp, Color(0xFF80CBC4), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = "Entorno Seguro Digital",
            tint = Color(0xFF80CBC4),
            modifier = Modifier.size(40.dp)
        )
    }

    Spacer(modifier = Modifier.height(14.dp))

    Text(
        text = "Asesorías Consultorias psicología",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        textAlign = TextAlign.Center
    )

    Text(
        text = "LivsForm Lykke • Entorno Seguro & Confidencial",
        fontSize = 12.5.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF80CBC4),
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(20.dp))

    // Main Card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 500.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF132B3C).copy(alpha = 0.94f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF80CBC4).copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Iniciar Sesión o Crear Cuenta",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Respalda tus reflexiones en el Diario Emocional Seguro, tus sesiones y tus diplomas en la nube de Firebase.",
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Frictionless Zero-Data Requirements Notice
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF004D40).copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF80CBC4).copy(alpha = 0.45f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF80CBC4),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Registro sin fricción: NO se requiere edad ni género. Solo se solicita tu cuenta de Google para garantizar la confidencialidad de tus datos.",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = Color(0xFFE0F2F1)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Feature Highlights Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FeaturePill(icon = Icons.Default.Lock, text = "Diario Encriptado", modifier = Modifier.weight(1f))
                FeaturePill(icon = Icons.Default.CloudSync, text = "Nube Firestore", modifier = Modifier.weight(1f))
                FeaturePill(icon = Icons.Default.MilitaryTech, text = "Diploma 10 Sesiones", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Optional Alias Input
            OutlinedTextField(
                value = userAlias,
                onValueChange = onUserAliasChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("user_alias_input"),
                label = { Text("Nombre o Alias personal (Opcional)") },
                placeholder = { Text("Ej: ViajeroSereno, Alex, etc.") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF80CBC4))
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF80CBC4),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color(0xFF80CBC4),
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    focusedPlaceholderColor = Color.White.copy(alpha = 0.4f),
                    unfocusedPlaceholderColor = Color.White.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Google Sign-In Button
            Button(
                onClick = onSignInClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("google_sign_in_button"),
                enabled = !isLoading,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00897B),
                    disabledContainerColor = Color(0xFF00897B).copy(alpha = 0.5f)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Continuar con Google",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Error Message Banner with dismiss
            AnimatedVisibility(visible = errorMessage != null) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Surface(
                        color = Color(0xFFB71C1C).copy(alpha = 0.25f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF8A80)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF8A80), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = errorMessage ?: "",
                                color = Color(0xFFFF8A80),
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = onDismissError, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFFFF8A80), modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Clinical Supervision Footnote
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color(0xFFFFD54F),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Supervisión institucional: tiemposdestinos@gmail.com",
                    fontSize = 11.sp,
                    color = Color(0xFFFFE082),
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Explore as Guest / Return Button
            TextButton(
                onClick = onExploreAsGuest,
                modifier = Modifier.testTag("explore_guest_button")
            ) {
                Text(
                    text = "Explorar en Modo Local sin Conexión",
                    color = Color(0xFF80CBC4),
                    fontSize = 13.sp
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "LivsForm Lykke • Contacto: asesoriaslykke@gmail.com",
        fontSize = 11.5.sp,
        color = Color.White.copy(alpha = 0.6f),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun FeaturePill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF0F2634),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF80CBC4).copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF80CBC4),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

/**
 * Attempts silent auto sign-in with Google using Credential Manager if already authorized.
 */
fun attemptAutoSignIn(
    context: Context,
    credentialManager: CredentialManager,
    onAuthSuccess: (FirebaseUser) -> Unit,
    onUnauthenticated: () -> Unit,
    scope: CoroutineScope
) {
    val existingUser = Firebase.auth.currentUser
    if (existingUser != null) {
        onAuthSuccess(existingUser)
        return
    }
    val clientId = try {
        context.getString(R.string.default_web_client_id)
    } catch (_: Exception) {
        onUnauthenticated()
        return
    }

    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(true)
        .setServerClientId(clientId)
        .setAutoSelectEnabled(true)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    scope.launch {
        try {
            val result = credentialManager.getCredential(context, request)
            val credential = result.credential
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                val authResult = Firebase.auth.signInWithCredential(authCredential).await()
                val user = authResult.user
                if (user != null) {
                    onAuthSuccess(user)
                } else {
                    onUnauthenticated()
                }
            } else {
                onUnauthenticated()
            }
        } catch (_: Exception) {
            onUnauthenticated()
        }
    }
}

/**
 * Executes interactive Google Sign-In using Credential Manager.
 */
fun onGoogleSignInClicked(
    context: Context,
    credentialManager: CredentialManager,
    customAlias: String? = null,
    onAuthSuccess: (FirebaseUser, String?) -> Unit,
    onAuthError: (String) -> Unit,
    scope: CoroutineScope,
    onAuthCancelled: () -> Unit = {}
) {
    val clientId = try {
        context.getString(R.string.default_web_client_id)
    } catch (_: Exception) {
        onAuthError("Configuración de Google Sign-In no disponible: default_web_client_id no encontrado")
        return
    }

    val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
    val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()

    scope.launch {
        try {
            val activity = context as? Activity ?: run {
                onAuthError("Error: Contexto de Activity no disponible")
                return@launch
            }
            val result = credentialManager.getCredential(activity, request)
            val credential = result.credential
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                val authResult = Firebase.auth.signInWithCredential(authCredential).await()
                val user = authResult.user
                if (user != null) {
                    onAuthSuccess(user, customAlias)
                } else {
                    onAuthError("No se pudo obtener el usuario autenticado de Firebase")
                }
            } else {
                onAuthError("Tipo de credencial no reconocido")
            }
        } catch (e: GetCredentialCancellationException) {
            Log.w("Auth", "Google Sign-In cancelado por el usuario: ${e.message}", e)
            onAuthCancelled()
        } catch (e: Exception) {
            Log.e("Auth", "Error al iniciar sesión con Google", e)
            onAuthError(e.localizedMessage ?: "No se pudo completar el inicio de sesión")
        }
    }
}

/**
 * Signs out of Firebase and clears Credential Manager state.
 */
fun performSignOut(
    context: Context,
    credentialManager: CredentialManager,
    onSignOutComplete: () -> Unit,
    scope: CoroutineScope
) {
    Firebase.auth.signOut()
    scope.launch {
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (e: Exception) {
            Log.e("Auth", "Error al limpiar estado de credenciales", e)
        } finally {
            onSignOutComplete()
        }
    }
}
