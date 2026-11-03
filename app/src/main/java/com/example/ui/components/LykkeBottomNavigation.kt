package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.NavigationScreen

data class NavItem(
    val screen: NavigationScreen,
    val label: String,
    val iconFilled: ImageVector,
    val iconOutlined: ImageVector,
    val tag: String
)

@Composable
fun LykkeBottomNavigation(
    currentScreen: NavigationScreen,
    onSelectScreen: (NavigationScreen) -> Unit,
    languageCode: String = "es",
    modifier: Modifier = Modifier
) {
    val stringsUI = remember(languageCode) { com.example.data.model.Localizer.get(languageCode) }
    val items = listOf(
        NavItem(NavigationScreen.HOME, stringsUI.menuInicio, Icons.Default.Home, Icons.Outlined.Home, "nav_home"),
        NavItem(NavigationScreen.TRACKER, stringsUI.menuTracker, Icons.Default.SelfImprovement, Icons.Outlined.SelfImprovement, "nav_tracker"),
        NavItem(NavigationScreen.LANGUAGES, stringsUI.botonIdiomas, Icons.Default.Translate, Icons.Outlined.Translate, "nav_languages"),
        NavItem(NavigationScreen.DIARY, stringsUI.menuDiario, Icons.Default.MenuBook, Icons.Outlined.MenuBook, "nav_diary"),
        NavItem(NavigationScreen.CHAT, stringsUI.menuChat, Icons.Default.ChatBubble, Icons.Outlined.ChatBubbleOutline, "nav_chat"),
        NavItem(NavigationScreen.GAMES, stringsUI.menuJuegos, Icons.Default.Quiz, Icons.Outlined.Quiz, "nav_games"),
        NavItem(NavigationScreen.BREATHING, stringsUI.menuRespira, Icons.Default.Air, Icons.Outlined.Air, "nav_breathing"),
        NavItem(NavigationScreen.SESSIONS_DIPLOMA, stringsUI.menuDiplomas, Icons.Default.WorkspacePremium, Icons.Outlined.WorkspacePremium, "nav_diploma"),
        NavItem(NavigationScreen.BOOKING, stringsUI.menuCitas, Icons.Default.VideoCall, Icons.Outlined.VideoCall, "nav_booking"),
        NavItem(NavigationScreen.SOLIDARITY, stringsUI.menuRegalos, Icons.Default.VolunteerActivism, Icons.Outlined.VolunteerActivism, "nav_solidarity")
    )

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        containerColor = Color(0xFF0C1924).copy(alpha = 0.95f),
        contentColor = Color.White,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEach { item ->
                val selected = currentScreen == item.screen
                NavigationBarItem(
                    selected = selected,
                    onClick = { onSelectScreen(item.screen) },
                    icon = {
                        Icon(
                            imageVector = if (selected) item.iconFilled else item.iconOutlined,
                            contentDescription = item.label,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF80DEEA),
                        selectedTextColor = Color(0xFF80DEEA),
                        indicatorColor = Color(0xFF004D40).copy(alpha = 0.6f),
                        unselectedIconColor = Color(0xFFB0BEC5),
                        unselectedTextColor = Color(0xFFB0BEC5)
                    ),
                    modifier = Modifier
                        .testTag(item.tag)
                        .padding(horizontal = 2.dp)
                )
            }
        }
    }
}
