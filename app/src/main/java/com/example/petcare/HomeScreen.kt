package com.example.petcare


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
//Creo las variables en inglés para hacer el nombre más corto y para mi así es más legible y fácil de distinguir
    //nombre de la mascota
    var petName by remember { mutableStateOf("Cargando...") }
    //fecha de nacimineto de la mascota
    var petBirthDate by remember { mutableStateOf("01/01/2000") }
    //Raza de la mascota
    var petBreed by remember { mutableStateOf("Desconocida") }
    //Recordatorios
    val reminders = remember { mutableStateOf(listOf<Map<String, String>>()) }

    // Carga los datos del perfil
    LaunchedEffect(currentUser) {
        currentUser?.let {
            firestore.collection("users").document(it.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        petName = document.getString("pet_name") ?: "Sin nombre"
                        petBirthDate = document.getString("pet_age") ?: "01/01/2000"
                        petBreed = document.getString("pet_breed") ?: "Desconocida"
                    } else {
                        petName = "Documento no encontrado"
                        petBreed = "Desconocida"
                    }
                }
                .addOnFailureListener { e ->
                    println("Error al cargar perfil: ${e.message}")
                    petName = "Error al cargar"
                    petBreed = "Error al cargar"
                }
        } ?: run {
            petName = "Usuario no autenticado"
            petBreed = "Desconocida"
        }
    }

    // Calcula la edad de la mascota
    val petAge = calculateAge(petBirthDate)

    // Carga recordatorios desde Firestore del usuario autenticado
    LaunchedEffect(Unit) {
        val userId = currentUser?.uid
        if (userId != null) {
            firestore.collection("reminders")
                .whereEqualTo("user_id", userId)  // Verifica el user_id
                .get()
                .addOnSuccessListener { result ->
                    val remindersList = result.map { document ->
                        mapOf(
                            "id" to document.id, // Guarda el ID del documento
                            "date" to (document.getString("date") ?: ""),
                            "reminder" to (document.getString("reminder") ?: "")
                        )
                    }
                    reminders.value = remindersList
                }
                .addOnFailureListener { e ->
                    println("Error al cargar recordatorios: ${e.message}")
                }
        }
    }

    //Cierre de sesión
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PetCare", fontSize = 20.sp, color = Color.White) },
                actions = {
                    IconButton(
                        onClick = {
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate("login") {
                                popUpTo(0)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Cerrar sesión",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6a994e)
                )
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFb5e48c))
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Sección del perfil
                ProfileSection(petName = petName, petAge = petAge, petBreed = petBreed)

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de recordatorios
                Text(
                    text = "Tus Recordatorios",
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(16.dp)
                )
                if (reminders.value.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(reminders.value) { reminder ->
                            ReminderCard(
                                date = reminder["date"] ?: "Sin fecha",
                                reminderText = reminder["reminder"] ?: "Sin recordatorio",
                                reminderId = reminder["id"] ?: "",
                                onDelete = { reminderId -> //Elimina recordatorio
                                    deleteReminder(
                                        firestore,
                                        reminderId,
                                        onSuccess = {
                                            reminders.value = reminders.value.filter { it["id"] != reminderId }
                                            println("Recordatorio eliminado correctamente")
                                        },
                                        onFailure = { e ->
                                            println("Error al eliminar recordatorio: ${e.message}")
                                        }
                                    )
                                }
                            )
                        }
                    }
                } else {
                    Text(
                        text = "No hay recordatorios disponibles.",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileSection(petName: String, petAge: String, petBreed: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFffffff), shape = MaterialTheme.shapes.medium)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Imagen de perfil
        Image(
            painter = painterResource(id = R.drawable.mascota),
            contentDescription = "Foto de perfil de la mascota",
            modifier = Modifier
                .size(80.dp)
                .background(Color.Gray, shape = MaterialTheme.shapes.small)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Información de la mascota
        Column {
            Text(text = "Nombre: $petName", fontSize = 18.sp, color = Color.Black)
            Text(text = "Edad: $petAge", fontSize = 16.sp, color = Color.DarkGray)
            Text(text = "Raza: $petBreed", fontSize = 16.sp, color = Color.DarkGray)
        }
    }
}

@Composable
fun ReminderCard(date: String, reminderText: String, reminderId: String, onDelete: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = MaterialTheme.shapes.medium)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = "Fecha: $date", fontSize = 16.sp, color = Color.Black)
            Text(text = "Recordatorio: $reminderText", fontSize = 14.sp, color = Color.DarkGray)
        }
        IconButton(onClick = { onDelete(reminderId) }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar recordatorio",
                tint = Color.Red
            )
        }
    }
}
//Función para borrar un recordatorio
fun deleteReminder(
    firestore: FirebaseFirestore,
    reminderId: String,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    firestore.collection("reminders").document(reminderId)
        .delete()
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onFailure(it) }
}

//Función que calcula la edad de una mascota partiendo de su fecha de nacimiento
fun calculateAge(birthDate: String): String {
    return try {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val birthLocalDate = LocalDate.parse(birthDate, formatter)
        val today = LocalDate.now()
        val age = Period.between(birthLocalDate, today)
        "${age.years} años y ${age.months} meses"
    } catch (e: Exception) {
        "Desconocida"
    }
}
//Barra de navegación
@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.Calendar,
        NavigationItem.AutoEvaluation,
        NavigationItem.Info
    )

    NavigationBar(
        containerColor = Color(0xFFb5e48c),
        contentColor = Color.White
    ) {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry.value?.destination

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(text = item.label) },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    if (currentDestination?.route != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

