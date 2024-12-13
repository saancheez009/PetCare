package com.example.petcare

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun AutoEvaluationScreen(navController: NavController) {
    val context = LocalContext.current

    // Lista de opciones para la evaluación
    val options = listOf(
        "Lamer patas", "Feliz", "Triste", "Eufórico", "Muy activo", "Tranquilo", "Poco activo",
        "He mordido cosas", "He gruñido", "He llorado", "Me he rascado mucho", "He vomitado",
        "Heces anormales", "Heces normales", "He jugado"
    )
//Opciones en las que el comportamiento de la mascota es anormal
    val problematicOptions = setOf(
        "Lamer patas", "Triste", "He mordido cosas", "He gruñido", "Poco activo", "He llorado",
        "Me he rascado mucho", "He vomitado", "Heces anormales"
    )

    //Opciones en las que el comportamiento es normal
    val happyOptions = setOf(
        "Feliz", "Eufórico", "Muy activo", "Tranquilo", "Poco activo", "He llorado", "He jugado"
    )

    val selectedOptions = remember { mutableStateMapOf<String, Boolean>() }
    var resultMessage by remember { mutableStateOf<String?>(null) }

    // Inicializa la selección
    options.forEach { option ->
        selectedOptions.putIfAbsent(option, false)
    }

    // Uso LaunchedEffect para limpiar las selecciones cuando salgo de la pantalla
    LaunchedEffect(key1 = navController) {
        // Limpia las selecciones cuando se regresa a la pantalla
        selectedOptions.clear()
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Autoevaluación de tu mascota",
                fontSize = 24.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Lista de opciones con casillas de verificación
            options.forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = selectedOptions[option] == true,
                        onCheckedChange = { isChecked ->
                            selectedOptions[option] = isChecked
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = option, fontSize = 16.sp, color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para evaluar
            Button(
                onClick = {
                    val selected = selectedOptions.filterValues { it }.keys
                    when {
                        selected.any { it in problematicOptions } -> {
                            resultMessage = "Parece que tu mascota está mal.¡Considera llevarla al veterinario!"
                            // Muestra el resultado en un Toast
                            Toast.makeText(context, resultMessage, Toast.LENGTH_SHORT).show()
                        }
                        selected.any { it in happyOptions } -> {
                            resultMessage = "Tu mascota parece feliz hoy :)"
                            Toast.makeText(context, resultMessage, Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            resultMessage = "Selecciona alguna opción para evaluar a tu mascota."
                            Toast.makeText(context, resultMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                //Texto del botón
                Text(text = "Evaluar", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Muestra el mensaje del resultado
            resultMessage?.let {
                Text(
                    text = it,
                    fontSize = 18.sp,
                    color = Color.Red,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}