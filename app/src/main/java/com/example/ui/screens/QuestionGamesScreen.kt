package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuizOption
import com.example.data.model.SectionCategory
import com.example.data.model.TextColorChoice
import com.example.ui.QuizSessionState

@Composable
fun QuestionGamesScreen(
    quizState: QuizSessionState,
    textColor: TextColorChoice,
    onSelectCategory: (SectionCategory) -> Unit,
    onSelectOption: (QuizOption) -> Unit,
    onNextQuestion: () -> Unit,
    onResetQuiz: () -> Unit
) {
    if (quizState.category == null) {
        // Category selection screen
        CategorySelectionView(
            textColor = textColor,
            onSelectCategory = onSelectCategory
        )
    } else if (quizState.isFinished) {
        // Quiz completed results view
        QuizCompletedView(
            quizState = quizState,
            textColor = textColor,
            onResetQuiz = onResetQuiz
        )
    } else {
        // Active Quiz Question View
        ActiveQuestionView(
            quizState = quizState,
            textColor = textColor,
            onSelectOption = onSelectOption,
            onNextQuestion = onNextQuestion,
            onCancelQuiz = onResetQuiz
        )
    }
}

@Composable
fun CategorySelectionView(
    textColor: TextColorChoice,
    onSelectCategory: (SectionCategory) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(bottom = 6.dp)) {
                Text(
                    text = "Juegos de Preguntas & Autoconocimiento",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor.color
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Elige una sección temática para reflexionar con preguntas guiadas y recibir comentarios psicológicos personalizados inmediatos.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = textColor.color.copy(alpha = 0.85f)
                )
            }
        }

        val categories = listOf(
            SectionCategory.STRESS to (Color(0xFFFFB74D) to Icons.Default.Spa),
            SectionCategory.FRUSTRATION to (Color(0xFFE57373) to Icons.Default.Psychology),
            SectionCategory.GOALS_DREAMS to (Color(0xFFFFD54F) to Icons.Default.EmojiEvents),
            SectionCategory.SELF_CARE to (Color(0xFF81C784) to Icons.Default.Favorite)
        )

        items(categories.size) { idx ->
            val (cat, style) = categories[idx]
            val (tint, icon) = style

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF132433).copy(alpha = 0.88f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectCategory(cat) }
                    .testTag("category_card_${cat.id}")
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(tint.copy(alpha = 0.20f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(28.dp))
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = cat.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = cat.description,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Iniciar",
                        tint = tint
                    )
                }
            }
        }
    }
}

@Composable
fun ActiveQuestionView(
    quizState: QuizSessionState,
    textColor: TextColorChoice,
    onSelectOption: (QuizOption) -> Unit,
    onNextQuestion: () -> Unit,
    onCancelQuiz: () -> Unit
) {
    val currentQuestion = quizState.questions.getOrNull(quizState.currentIndex) ?: return

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Category indicator header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onCancelQuiz) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color(0xFF80DEEA))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cambiar tema", color = Color(0xFF80DEEA), fontSize = 12.sp)
                }

                Text(
                    text = "Pregunta ${quizState.currentIndex + 1} de ${quizState.questions.size}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD54F)
                )
            }
        }

        // Question card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF102A3D).copy(alpha = 0.90f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = quizState.category?.title ?: "Autoconocimiento",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF80CBC4)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentQuestion.question,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp,
                        color = textColor.color
                    )
                }
            }
        }

        // Options list
        items(currentQuestion.options.size) { index ->
            val option = currentQuestion.options[index]
            val isSelected = quizState.selectedOption == option

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected)
                        Color(0xFF004D40).copy(alpha = 0.85f)
                    else
                        Color(0xFF13222F).copy(alpha = 0.80f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) Color(0xFF26A69A) else Color.White.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable(enabled = !quizState.isShowingComment) {
                        onSelectOption(option)
                    }
                    .testTag("quiz_option_$index")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color(0xFF80CBC4) else Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ('A' + index).toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isSelected) Color(0xFF00332C) else Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Text(
                        text = option.text,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = Color.White
                    )
                }
            }
        }

        // Psychological Commentary & Tip Card
        if (quizState.isShowingComment && quizState.selectedOption != null) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF183B38).copy(alpha = 0.95f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("psychological_feedback_card")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = Color(0xFF80CBC4),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Comentario Psicológico LivsForm Lykke",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF80CBC4)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = quizState.selectedOption.psychologicalComment,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF004D40).copy(alpha = 0.5f))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD54F),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Acción Sugerida:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFD54F)
                                    )
                                    Text(
                                        text = quizState.selectedOption.tip,
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onNextQuestion,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26A69A)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("next_question_button")
                        ) {
                            Text(
                                text = if (quizState.currentIndex + 1 < quizState.questions.size)
                                    "Siguiente Pregunta"
                                else
                                    "Finalizar y Registrar Sesión",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF002922)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuizCompletedView(
    quizState: QuizSessionState,
    textColor: TextColorChoice,
    onResetQuiz: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2E7D32).copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF81C784),
                    modifier = Modifier.size(46.dp)
                )
            }
        }

        item {
            Text(
                text = "¡Juego de Reflexión Completado!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor.color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Se ha acreditado 1 nueva sesión a tu progreso hacia el Diploma Virtual y la Reflexión de 10 Sesiones.",
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = textColor.color.copy(alpha = 0.85f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF10293C).copy(alpha = 0.90f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Síntesis del Aprendizaje",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF80CBC4)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Tema trabajado: ${quizState.category?.title}\n" +
                               "• Nivel de autoconsciencia: Elevado\n" +
                               "• Recordatorio clave: La transformación de tus hábitos y emociones no requiere perfección, sino paciencia constante y compasión hacia tus propios tiempos.",
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = Color.White
                    )
                }
            }
        }

        item {
            Button(
                onClick = onResetQuiz,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("play_another_quiz_btn")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Explorar otra sección de preguntas", fontWeight = FontWeight.Bold)
            }
        }
    }
}
