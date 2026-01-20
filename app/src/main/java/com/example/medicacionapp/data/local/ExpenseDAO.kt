package com.example.medicacionapp.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDAO {
    @Query("SELECT * FROM medicamentos ORDER BY fechaInicio DESC")
    fun obtenerTodosMedicamentos(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM medicamentos WHERE activo = 1 ORDER BY fechaInicio DESC")
    fun obtenerMedicamentosActivos(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM medicamentos WHERE id = :id")
    suspend fun obtenerMedicamentoPorId(id: Long): ExpenseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarMedicamento(medicamento: ExpenseEntity): Long

    @Update
    suspend fun actualizarMedicamento(medicamento: ExpenseEntity)

    @Delete
    suspend fun eliminarMedicamento(medicamento: ExpenseEntity)

    @Query("DELETE FROM medicamentos")
    suspend fun eliminarTodosMedicamentos()
}