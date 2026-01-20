package com.example.medicacionapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicamentos")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val descripcion: String,
    val dosis: String,
    val frecuencia: String, // Ejemplo: "Cada 8 horas", "3 veces al día"
    val horarios: String, // Lista de horarios separados por comas: "08:00,14:00,20:00"
    val fechaInicio: Long,
    val fechaFin: Long? = null,
    val activo: Boolean = true,
    val color: String = "#4CAF50" // Color para identificar el medicamento
)