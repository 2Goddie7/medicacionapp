package com.example.medicacionapp.data.repository

import android.content.Context
import com.example.medicacionapp.data.local.AppDatabase
import com.example.medicacionapp.data.local.ExpenseEntity
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val medicamentoDao = database.expenseDao()

    fun obtenerTodosMedicamentos(): Flow<List<ExpenseEntity>> {
        return medicamentoDao.obtenerTodosMedicamentos()
    }

    fun obtenerMedicamentosActivos(): Flow<List<ExpenseEntity>> {
        return medicamentoDao.obtenerMedicamentosActivos()
    }

    suspend fun obtenerMedicamentoPorId(id: Long): ExpenseEntity? {
        return medicamentoDao.obtenerMedicamentoPorId(id)
    }

    suspend fun insertarMedicamento(medicamento: ExpenseEntity): Long {
        return medicamentoDao.insertarMedicamento(medicamento)
    }

    suspend fun actualizarMedicamento(medicamento: ExpenseEntity) {
        medicamentoDao.actualizarMedicamento(medicamento)
    }

    suspend fun eliminarMedicamento(medicamento: ExpenseEntity) {
        medicamentoDao.eliminarMedicamento(medicamento)
    }

    suspend fun eliminarTodosMedicamentos() {
        medicamentoDao.eliminarTodosMedicamentos()
    }
}