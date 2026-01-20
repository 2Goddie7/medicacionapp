package com.example.medicacionapp.ui.theme

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicacionapp.data.local.ExpenseEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(viewModel: ExpenseViewModel = viewModel()) {
    val medicamentos by viewModel.medicamentos.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }
    var medicamentoSeleccionado by remember { mutableStateOf<ExpenseEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            "Mis Medicamentos",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { /* Configuración */ }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Configuración",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    medicamentoSeleccionado = null
                    mostrarDialogo = true
                },
                icon = {
                    Icon(Icons.Default.Add, contentDescription = null)
                },
                text = { Text("Agregar Medicamento") },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            if (medicamentos.isEmpty()) {
                EstadoVacio()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = medicamentos,
                        key = { it.id }
                    ) { medicamento ->
                        TarjetaMedicamento(
                            medicamento = medicamento,
                            onEditar = {
                                medicamentoSeleccionado = medicamento
                                mostrarDialogo = true
                            },
                            onEliminar = { viewModel.eliminarMedicamento(medicamento) },
                            onToggleActivo = {
                                viewModel.actualizarMedicamento(
                                    medicamento.copy(activo = !medicamento.activo)
                                )
                            }
                        )
                    }
                }
            }
        }

        if (mostrarDialogo) {
            DialogoMedicamento(
                medicamento = medicamentoSeleccionado,
                onDismiss = {
                    mostrarDialogo = false
                    medicamentoSeleccionado = null
                },
                onGuardar = { medicamento ->
                    if (medicamentoSeleccionado == null) {
                        viewModel.agregarMedicamento(medicamento)
                    } else {
                        viewModel.actualizarMedicamento(medicamento)
                    }
                    mostrarDialogo = false
                    medicamentoSeleccionado = null
                }
            )
        }
    }
}

@Composable
fun TarjetaMedicamento(
    medicamento: ExpenseEntity,
    onEditar: () -> Unit,
    onEliminar: () -> Unit,
    onToggleActivo: () -> Unit
) {
    var expandido by remember { mutableStateOf(false) }
    val colorMedicamento = Color(android.graphics.Color.parseColor(medicamento.color))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            // Cabecera de la tarjeta
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                colorMedicamento.copy(alpha = 0.8f),
                                colorMedicamento.copy(alpha = 0.6f)
                            )
                        )
                    )
                    .clickable { expandido = !expandido }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono de medicamento
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Medication,
                        contentDescription = null,
                        tint = colorMedicamento,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Información del medicamento
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = medicamento.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = medicamento.dosis,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                // Indicador de estado
                Switch(
                    checked = medicamento.activo,
                    onCheckedChange = { onToggleActivo() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = VerdeMedicina,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = ColorInactivo
                    )
                )
            }

            // Contenido expandible
            AnimatedVisibility(
                visible = expandido,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Descripción
                    if (medicamento.descripcion.isNotEmpty()) {
                        InfoRow(
                            icono = Icons.Default.Description,
                            titulo = "Descripción",
                            contenido = medicamento.descripcion
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Frecuencia
                    InfoRow(
                        icono = Icons.Default.Schedule,
                        titulo = "Frecuencia",
                        contenido = medicamento.frecuencia
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Horarios
                    InfoRow(
                        icono = Icons.Default.AccessTime,
                        titulo = "Horarios",
                        contenido = medicamento.horarios.replace(",", " • ")
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Fecha de inicio
                    val fechaInicio = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .format(Date(medicamento.fechaInicio))
                    InfoRow(
                        icono = Icons.Default.CalendarToday,
                        titulo = "Inicio",
                        contenido = fechaInicio
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    // Botones de acción
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedButton(
                            onClick = onEditar,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Editar")
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        OutlinedButton(
                            onClick = onEliminar,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = RojoAlerta
                            )
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Eliminar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(icono: androidx.compose.ui.graphics.vector.ImageVector, titulo: String, contenido: String) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelMedium,
                color = TextoSecundario,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = contenido,
                style = MaterialTheme.typography.bodyMedium,
                color = TextoPrincipal
            )
        }
    }
}

@Composable
fun EstadoVacio() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.MedicalServices,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "No tienes medicamentos registrados",
                style = MaterialTheme.typography.titleLarge,
                color = TextoSecundario,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Toca el botón '+' para agregar tu primer medicamento",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoTerciario
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoMedicamento(
    medicamento: ExpenseEntity?,
    onDismiss: () -> Unit,
    onGuardar: (ExpenseEntity) -> Unit
) {
    var nombre by remember { mutableStateOf(medicamento?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(medicamento?.descripcion ?: "") }
    var dosis by remember { mutableStateOf(medicamento?.dosis ?: "") }

    // Extraer la primera hora de los horarios existentes o usar 8:00 por defecto
    val horaInicial = medicamento?.horarios?.split(",")?.firstOrNull()?.trim()?.split(":")
    var horaSeleccionada by remember {
        mutableStateOf(horaInicial?.getOrNull(0)?.toIntOrNull() ?: 8)
    }
    var minutoSeleccionado by remember {
        mutableStateOf(horaInicial?.getOrNull(1)?.toIntOrNull() ?: 0)
    }

    var mostrarTimePicker by remember { mutableStateOf(false) }
    var colorSeleccionado by remember {
        mutableStateOf(medicamento?.color ?: "#4CAF50")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (medicamento == null) Icons.Default.Add else Icons.Default.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    if (medicamento == null) "Nuevo Medicamento" else "Editar Medicamento",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Campo: Nombre del medicamento
                item {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre del medicamento *") },
                        leadingIcon = {
                            Icon(Icons.Default.Medication, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // Campo: Hora (con TimePicker)
                item {
                    OutlinedTextField(
                        value = String.format("%02d:%02d", horaSeleccionada, minutoSeleccionado),
                        onValueChange = { },
                        label = { Text("Hora *") },
                        leadingIcon = {
                            Icon(Icons.Default.AccessTime, contentDescription = null)
                        },
                        trailingIcon = {
                            IconButton(onClick = { mostrarTimePicker = true }) {
                                Icon(Icons.Default.Schedule, contentDescription = "Seleccionar hora")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        singleLine = true
                    )
                }

                // Campo: Dosis
                item {
                    OutlinedTextField(
                        value = dosis,
                        onValueChange = { dosis = it },
                        label = { Text("Dosis *") },
                        placeholder = { Text("Ej: 500mg, 2 tabletas, 10ml") },
                        leadingIcon = {
                            Icon(Icons.Default.MedicalServices, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // Campo: Descripción (opcional)
                item {
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción (opcional)") },
                        placeholder = { Text("Para qué es este medicamento") },
                        leadingIcon = {
                            Icon(Icons.Default.Description, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }

                // Selector de color
                item {
                    Text(
                        "Color identificador",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listColoresMedicamentos.forEach { color ->
                            val colorHex = "#${Integer.toHexString(color.hashCode()).substring(0, 6)}"
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .clickable { colorSeleccionado = colorHex }
                                    .then(
                                        if (colorSeleccionado == colorHex) {
                                            Modifier.padding(2.dp)
                                        } else Modifier
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (colorSeleccionado == colorHex) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nombre.isNotBlank() && dosis.isNotBlank()) {
                        val horarioFormateado = String.format("%02d:%02d", horaSeleccionada, minutoSeleccionado)
                        onGuardar(
                            ExpenseEntity(
                                id = medicamento?.id ?: 0,
                                nombre = nombre,
                                descripcion = descripcion,
                                dosis = dosis,
                                frecuencia = "Diaria", // Valor por defecto
                                horarios = horarioFormateado, // Solo un horario
                                fechaInicio = medicamento?.fechaInicio ?: System.currentTimeMillis(),
                                activo = medicamento?.activo ?: true,
                                color = colorSeleccionado
                            )
                        )
                    }
                },
                enabled = nombre.isNotBlank() && dosis.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )

    // TimePicker Dialog
    if (mostrarTimePicker) {
        TimePickerDialog(
            horaInicial = horaSeleccionada,
            minutoInicial = minutoSeleccionado,
            onConfirm = { hora, minuto ->
                horaSeleccionada = hora
                minutoSeleccionado = minuto
                mostrarTimePicker = false
            },
            onDismiss = { mostrarTimePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    horaInicial: Int,
    minutoInicial: Int,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = horaInicial,
        initialMinute = minutoInicial,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Seleccionar hora",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            TimePicker(
                state = timePickerState,
                modifier = Modifier.padding(16.dp)
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(timePickerState.hour, timePickerState.minute)
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}