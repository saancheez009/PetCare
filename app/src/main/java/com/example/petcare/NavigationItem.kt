package com.example.petcare
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.ui.graphics.vector.ImageVector
//Barra de navegación
sealed class NavigationItem(val route: String, val label: String, val icon: ImageVector) {
    object Home : NavigationItem("home", "Inicio", Icons.Default.Home)
    object Calendar : NavigationItem("calendar", "Calendario", Icons.Default.CalendarToday)
    object AutoEvaluation : NavigationItem("evaluation", "Evaluación", Icons.Default.Assessment)
    object Info : NavigationItem("info", "Info", Icons.Default.Info)
}