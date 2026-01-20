package com.example.medicacionapp.ui.theme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicacionapp.data.repository.ExpenseRepository
import com.example.medicacionapp.alarm.ReminderSchedule
import com.example.medicacionapp.data.local.ExpenseEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ExpenseRepository(application)
    private val reminderSchedule = ReminderSchedule(application)

    private val _medicamentos = MutableStateFlow<List<ExpenseEntity>>(emptyList())
    val medicamentos: StateFlow<List<ExpenseEntity>> = _medicamentos.asStateFlow()

    init {
        cargarMedicamentos()
    }

    private fun cargarMedicamentos() {
        viewModelScope.launch {
            repository.obtenerTodosMedicamentos().collect { listaMedicamentos ->
                _medicamentos.value = listaMedicamentos
            }
        }
    }

    fun agregarMedicamento(medicamento: ExpenseEntity) {
        viewModelScope.launch {
            val id = repository.insertarMedicamento(medicamento)
            val medicamentoConId = medicamento.copy(id = id)

            // Programar recordatorios
            if (medicamentoConId.activo) {
                reminderSchedule.programarRecordatorios(medicamentoConId)
            }
        }
    }

    fun actualizarMedicamento(medicamento: ExpenseEntity) {
        viewModelScope.launch {
            repository.actualizarMedicamento(medicamento)

            // Reprogramar recordatorios
            if (medicamento.activo) {
                reminderSchedule.programarRecordatorios(medicamento)
            } else {
                reminderSchedule.cancelarRecordatorios(medicamento.id)
            }
        }
    }

    fun eliminarMedicamento(medicamento: ExpenseEntity) {
        viewModelScope.launch {
            repository.eliminarMedicamento(medicamento)
            reminderSchedule.cancelarRecordatorios(medicamento.id)
        }
    }

    fun reprogramarTodosLosRecordatorios() {
        viewModelScope.launch {
            reminderSchedule.reprogramarTodosLosRecordatorios(_medicamentos.value)
        }
    }
}