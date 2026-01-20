package com.example.medicacionapp.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.medicacionapp.data.local.ExpenseEntity
import java.util.Calendar

class ReminderSchedule(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun programarRecordatorios(medicamento: ExpenseEntity) {
        if (!medicamento.activo) {
            cancelarRecordatorios(medicamento.id)
            return
        }

        val horarios = medicamento.horarios.split(",")

        horarios.forEachIndexed { index, horario ->
            if (horario.isNotBlank()) {
                programarRecordatorio(
                    medicamento,
                    horario.trim(),
                    index
                )
            }
        }
    }

    private fun programarRecordatorio(medicamento: ExpenseEntity, horario: String, index: Int) {
        try {
            val partesHorario = horario.split(":")
            if (partesHorario.size != 2) return

            val hora = partesHorario[0].toIntOrNull() ?: return
            val minuto = partesHorario[1].toIntOrNull() ?: return

            val calendario = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hora)
                set(Calendar.MINUTE, minuto)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                // Si la hora ya pasó hoy, programar para mañana
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }

            val intent = Intent(context, AlarmReceiver::class.java).apply {
                // IMPORTANTE: Cambiar el action para que coincida con el AndroidManifest
                action = "com.example.medicacionapp.MEDICAMENTO_ALARM"
                putExtra("medicamento_nombre", medicamento.nombre)
                putExtra("medicamento_dosis", medicamento.dosis)
                putExtra("medicamento_id", medicamento.id)
            }

            val requestCode = generarRequestCode(medicamento.id, index)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // IMPORTANTE: Usar setRepeating en lugar de setExactAndAllowWhileIdle para alarmas repetitivas
            // setExactAndAllowWhileIdle es para alarmas únicas
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendario.timeInMillis,
                    AlarmManager.INTERVAL_DAY, // Repetir cada 24 horas
                    pendingIntent
                )
            } else {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendario.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelarRecordatorios(medicamentoId: Long) {
        // Cancelar hasta 10 horarios posibles por medicamento
        for (index in 0..9) {
            val requestCode = generarRequestCode(medicamentoId, index)
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                action = "com.example.medicacionapp.MEDICAMENTO_ALARM"
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    private fun generarRequestCode(medicamentoId: Long, index: Int): Int {
        return (medicamentoId * 100 + index).toInt()
    }

    fun reprogramarTodosLosRecordatorios(medicamentos: List<ExpenseEntity>) {
        medicamentos.forEach { medicamento ->
            if (medicamento.activo) {
                programarRecordatorios(medicamento)
            }
        }
    }
}