@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.petcare

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar


@Composable
fun CalendarScreen(navController: NavController) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    //Autenticacion del usuario
    val auth = FirebaseAuth.getInstance()
    val selectedDate = remember { mutableStateOf("") }
    val reminder = remember { mutableStateOf("") }

    // Función para abrir el DatePicker
    fun openDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                selectedDate.value = "$dayOfMonth/${month + 1}/$year"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Función para verificar si la fecha seleccionada es anterior a la fecha actual
    fun isDateInThePast(date: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("d/M/yyyy")
        return try {
            val selectedDateLocal = LocalDate.parse(date, formatter)
            val today = LocalDate.now()
            selectedDateLocal.isBefore(today)
        } catch (e: Exception) {
            false  //Devuelve false si la fecha ya ha pasado
        }
    }

    // Función para programar la notificación
    fun scheduleNotification(date: String, reminderText: String) {
        val formatter = DateTimeFormatter.ofPattern("d/M/yyyy")
        try {
            val selectedDateLocal = LocalDate.parse(date, formatter)
            val calendar = Calendar.getInstance()
            calendar.set(selectedDateLocal.year, selectedDateLocal.monthValue - 1, selectedDateLocal.dayOfMonth, 9, 0, 0) // Establecer a las 9:00 AM del día seleccionado

            val intent = Intent(context, NotificationReceiver::class.java).apply {
                putExtra("reminder_text", reminderText)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            // Programa la alarma
            //Para la alarma se han añadido permisos en el android manifest
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } catch (e: Exception) {
            Log.e("CalendarScreen", "Error al programar la notificación: ${e.message}")
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Calendario de Recordatorios", fontSize = 24.sp, color = Color.Black)

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de fecha
            Button(onClick = { openDatePicker() }) {
                Icon(imageVector = Icons.Default.Event, contentDescription = null)
                Text(text = if (selectedDate.value.isEmpty()) "Seleccionar Fecha" else selectedDate.value)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de texto para añadir el nombre del recordatorio
            OutlinedTextField(
                value = reminder.value,
                onValueChange = { reminder.value = it },
                label = { Text("Añadir recordatorio") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val userId = auth.currentUser?.uid

                    if (userId != null && selectedDate.value.isNotEmpty() && reminder.value.isNotEmpty()) {
                        // Verifica si la fecha seleccionada es anterior a la fecha actual
                        if (isDateInThePast(selectedDate.value)) {
                            Toast.makeText(context, "No se puede añadir un recordatorio para una fecha pasada", Toast.LENGTH_SHORT).show()
                        } else {
                            val data = hashMapOf(
                                "date" to selectedDate.value,
                                "reminder" to reminder.value,
                                "user_id" to userId,
                                "created_at" to System.currentTimeMillis()
                            )

                            // Guarda el recordatorio en Firestore
                            firestore.collection("reminders")
                                .add(data)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Recordatorio añadido", Toast.LENGTH_SHORT).show()

                                    // Programa la notificación
                                    scheduleNotification(selectedDate.value, reminder.value)

                                    // Limpia los campos después de añadir el recordatorio para volver a añadir otro si asi se desea
                                    selectedDate.value = ""
                                    reminder.value = ""
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        // Muestra un mensaje si hay campos vacíos o el usuario no está autenticado
                        Toast.makeText(context, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Guardar Recordatorio")
            }
        }
    }
}