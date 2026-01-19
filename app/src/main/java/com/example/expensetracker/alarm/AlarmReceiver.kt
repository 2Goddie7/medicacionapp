package com.example.medicacionapp.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.example.medicacionapp.MainActivity
import com.example.medicacionapp.R

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "medicamento_channel"
        private const val NOTIFICATION_ID_BASE = 1000
    }

    override fun onReceive(context: Context, intent: Intent) {
        val medicamentoNombre = intent.getStringExtra("medicamento_nombre") ?: "Medicamento"
        val medicamentoDosis = intent.getStringExtra("medicamento_dosis") ?: ""
        val medicamentoId = intent.getLongExtra("medicamento_id", 0)

        crearCanalNotificacion(context)
        mostrarNotificacion(context, medicamentoNombre, medicamentoDosis, medicamentoId)

        // Activar vibración
        activarVibracion(context)
    }

    private fun crearCanalNotificacion(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nombre = context.getString(R.string.notification_channel_name)
            val descripcion = context.getString(R.string.notification_channel_description)
            val importancia = NotificationManager.IMPORTANCE_HIGH

            val canal = NotificationChannel(CHANNEL_ID, nombre, importancia).apply {
                description = descripcion
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 500)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    null
                )
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }
    }

    private fun mostrarNotificacion(
        context: Context,
        medicamentoNombre: String,
        medicamentoDosis: String,
        medicamentoId: Long
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("medicamento_id", medicamentoId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            medicamentoId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val sonidoNotificacion = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificacion = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.tomar_medicamento))
            .setContentText("${context.getString(R.string.no_olvides)} $medicamentoNombre - $medicamentoDosis")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${context.getString(R.string.no_olvides)} $medicamentoNombre - $medicamentoDosis")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(sonidoNotificacion)
            .setVibrate(longArrayOf(0, 500, 200, 500, 200, 500))
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID_BASE + medicamentoId.toInt(), notificacion)
    }

    private fun activarVibracion(context: Context) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val patron = longArrayOf(0, 500, 200, 500, 200, 500)
            vibrator.vibrate(VibrationEffect.createWaveform(patron, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 500, 200, 500, 200, 500), -1)
        }
    }
}