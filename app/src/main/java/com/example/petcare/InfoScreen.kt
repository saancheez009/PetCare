package com.example.petcare

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun InfoScreen(navController: NavController) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Información para el cuidado de mascotas",
                fontSize = 24.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sección alimentación
            Text(text = "🍴 Alimentación", fontSize = 20.sp, color = Color(0xFF6a994e))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Proporciona una dieta equilibrada según la especie, edad y tamaño de tu mascota. Consulta a un veterinario para elegir el alimento adecuado.",
                fontSize = 16.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sección higiene
            Text(text = "🛁 Higiene", fontSize = 20.sp, color = Color(0xFF6a994e))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Mantén una buena higiene cepillando el pelaje regularmente, limpiando sus orejas y cortando sus uñas según sea necesario.",
                fontSize = 16.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sección bienestar emocional
            Text(text = "❤️ Bienestar emocional", fontSize = 20.sp, color = Color(0xFF6a994e))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Dedica tiempo a jugar y ejercitar a tu mascota para mantenerla activa y feliz. Asegúrate de que tenga un lugar seguro y cómodo para descansar.",
                fontSize = 16.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sección Salud
            Text(text = "🐾 Salud", fontSize = 20.sp, color = Color(0xFF6a994e))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Visita al veterinario regularmente para vacunas, chequeos y cualquier problema de salud. Presta atención a señales de enfermedad como falta de apetito o inactividad",
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
}