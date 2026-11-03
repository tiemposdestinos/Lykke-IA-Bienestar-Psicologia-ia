package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.PlayStationJazzIntroPlayer
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin

/**
 * Pantalla de Inicio Cinemática Estilo PlayStation con Intro de Jazz Electrónico.
 * Despliega el símbolo de LivsForm Lykke emergiendo de la oscuridad con halo cromático,
 * anillos de resonancia cuántica y acorde de sintetizador jazz envolvente.
 * Duración calibrada a 3.8 segundos para una experiencia completa antes de ingresar a la app.
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    // Reproducir acorde de inicio cinemático estilo PlayStation / Jazz Electrónico
    DisposableEffect(Unit) {
        PlayStationJazzIntroPlayer.playIntroSound(volume = 0.90f)
        onDispose {
            PlayStationJazzIntroPlayer.stop()
        }
    }

    // Duración cinematográfica estilo PlayStation calibrada a ~42 segundos (aprox 45s o un poco menos)
    LaunchedEffect(Unit) {
        delay(42000)
        onSplashFinished()
    }

    // Transición infinita para halo, brillo, respiración cuántica y rotación 3D del círculo
    val infiniteTransition = rememberInfiniteTransition(label = "PlayStationSplashLoop")

    // Rotaciones 3D continuas del círculo y anillos giroscópicos
    val rot3DY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rot3DY"
    )

    val rot3DX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rot3DX"
    )

    val rot3DZ by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rot3DZ"
    )

    val rot3DInverse by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(4800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rot3DInverse"
    )

    // Expansión de anillo tipo radar/PlayStation
    val ringExpansion by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RingExpansion"
    )

    val ringAlpha by infiniteTransition.animateFloat(
        initialValue = 0.70f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RingAlpha"
    )

    // Pulso del aura central
    val auraPulse by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AuraPulse"
    )

    // Animación de Entrada Progresiva (Estilo PlayStation: zoom suave desde el infinito)
    var startAnimation by remember { mutableStateOf(false) }

    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.35f,
        animationSpec = spring(
            dampingRatio = 0.72f,
            stiffness = Spring.StiffnessLow
        ),
        label = "LogoScale"
    )

    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(900, easing = EaseOutCubic),
        label = "LogoAlpha"
    )

    val textAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1200, delayMillis = 650, easing = EaseOutCubic),
        label = "TextAlpha"
    )

    val letterSpacingAnim by animateFloatAsState(
        targetValue = if (startAnimation) 7f else 1f,
        animationSpec = tween(1600, easing = EaseOutQuart),
        label = "LetterSpacing"
    )

    LaunchedEffect(Unit) {
        delay(100)
        startAnimation = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0F1E36), // Azul noche profundo
                        Color(0xFF070E1E),
                        Color(0xFF020409)  // Negro abisal PlayStation
                    )
                )
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                PlayStationJazzIntroPlayer.stop()
                onSplashFinished()
            }
            .testTag("lykke_splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // 1. Rayo de Luz / Resplandor Radial de Fondo (Aura Estilo PlayStation)
        Box(
            modifier = Modifier
                .size(340.dp)
                .scale(auraPulse)
                .alpha(0.40f)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF38BDF8),
                            Color(0xFF0284C7),
                            Color(0xFFFFD54F).copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    )
                )
        )

        // 2. Anillos Ondulantes Expansivos (PlayStation Waves)
        Box(
            modifier = Modifier
                .size(240.dp)
                .scale(ringExpansion)
                .alpha(ringAlpha)
                .border(
                    width = 2.dp,
                    brush = Brush.sweepGradient(
                        listOf(
                            Color(0xFF38BDF8),
                            Color(0xFFFFD54F),
                            Color(0xFF4ADE80),
                            Color(0xFF38BDF8)
                        )
                    ),
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(190.dp)
                .scale((ringExpansion * 0.85f).coerceAtLeast(0.7f))
                .alpha(ringAlpha * 0.8f)
                .border(
                    width = 1.5.dp,
                    color = Color(0xFF38BDF8).copy(alpha = 0.6f),
                    shape = CircleShape
                )
        )

        // 3. Contenedor Central: Emblema LivsForm Lykke y Tipografía PlayStation
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(logoScale)
                .alpha(logoAlpha)
        ) {
            // Insignia Central con Efecto 3D de Círculo Girando (Giroscopio Cuántico PlayStation)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(240.dp)
            ) {
                // Anillo Orbital 3D 1: Girando en el eje Y a 45 grados de inclinación
                Box(
                    modifier = Modifier
                        .size(228.dp)
                        .graphicsLayer {
                            rotationY = rot3DY
                            rotationX = 45f
                            cameraDistance = 16f * density
                        }
                        .border(
                            width = 2.2.dp,
                            brush = Brush.sweepGradient(
                                listOf(
                                    Color(0xFFFFD54F),
                                    Color(0xFF38BDF8),
                                    Color(0xFF4ADE80),
                                    Color(0xFFFFD54F)
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Anillo Orbital 3D 2: Girando en sentido inverso en eje Y e inclinado en -52 grados
                Box(
                    modifier = Modifier
                        .size(208.dp)
                        .graphicsLayer {
                            rotationY = rot3DInverse
                            rotationX = -52f
                            rotationZ = rot3DZ
                            cameraDistance = 16f * density
                        }
                        .border(
                            width = 1.8.dp,
                            brush = Brush.sweepGradient(
                                listOf(
                                    Color(0xFF38BDF8),
                                    Color(0xFFC084FC),
                                    Color(0xFFFFD54F),
                                    Color(0xFF38BDF8)
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Anillo Orbital 3D 3: Ecuatorial rotando en X y Z
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .graphicsLayer {
                            rotationX = rot3DX
                            rotationY = rot3DY * 0.7f
                            cameraDistance = 16f * density
                        }
                        .border(
                            width = 1.4.dp,
                            brush = Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF38BDF8).copy(alpha = 0.8f),
                                    Color(0xFFFFD54F).copy(alpha = 0.4f),
                                    Color(0xFF4ADE80).copy(alpha = 0.8f)
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Círculo Central con Profundidad 3D y Balanceo Tridimensional Continuo
                val tiltY = (sin(rot3DY * (PI.toFloat() / 180f)) * 14f)
                val tiltX = (sin(rot3DX * (PI.toFloat() / 180f)) * 8f)

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(175.dp)
                        .graphicsLayer {
                            rotationY = tiltY
                            rotationX = tiltX
                            cameraDistance = 18f * density
                        }
                        .clip(CircleShape)
                        .background(Color(0xFF0E192F))
                        .border(
                            width = 2.5.dp,
                            brush = Brush.linearGradient(
                                listOf(
                                    Color(0xFFFFD54F),
                                    Color(0xFF38BDF8),
                                    Color(0xFF4ADE80),
                                    Color(0xFFFFD54F)
                                )
                            ),
                            shape = CircleShape
                        )
                ) {
                    // Logotipo oficial con fondo azul que contiene la imagen del cerebro (I.A. Conciencia)
                    Image(
                        painter = painterResource(id = R.drawable.ic_logo_app_icon_circular),
                        contentDescription = "Logotipo Oficial Lykke Conciencia I.A.",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Título PlayStation Elegante con Espaciado Holográfico
            Text(
                text = "LYKKE",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = letterSpacingAnim.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(textAlpha)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtítulo Dorado con Tipografía de Consola
            Text(
                text = "LIVSFORM • PSICOLOGÍA & BIENESTAR HUMANO",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.5.sp,
                color = Color(0xFFFFD54F),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(textAlpha)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Armonía Danesa • Confidencialidad & Resiliencia",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(textAlpha)
            )

            Spacer(modifier = Modifier.height(26.dp))

            // Indicador sutil de toque para entrar directo
            Text(
                text = "Toca la pantalla para iniciar de inmediato ✦",
                fontSize = 10.sp,
                color = Color(0xFF38BDF8).copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(textAlpha * 0.8f)
            )
        }
    }
}
