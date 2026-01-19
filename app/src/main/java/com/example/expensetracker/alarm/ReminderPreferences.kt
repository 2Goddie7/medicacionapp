package com.example.medicacionapp.alarm

import android.content.Context
import android.content.SharedPreferences

class ReminderPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "medicamento_preferences",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        private const val KEY_NOTIFICATION_ENABLED = "notification_enabled"
    }

    var sonidoHabilitado: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()

    var vibracionHabilitada: Boolean
        get() = prefs.getBoolean(KEY_VIBRATION_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, value).apply()

    var notificacionesHabilitadas: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATION_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIFICATION_ENABLED, value).apply()
}