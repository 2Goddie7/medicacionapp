package com.example.medicacionapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.medicacionapp.ui.theme.ExpenseScreen
import com.example.medicacionapp.ui.theme.ExpenseTrackerTheme
import com.example.medicacionapp.ui.theme.ExpenseViewModel

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: ExpenseViewModel

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permiso concedido, reprogramar recordatorios
                viewModel.reprogramarTodosLosRecordatorios()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]

        // Solicitar permisos necesarios
        solicitarPermisos()

        setContent {
            ExpenseTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ExpenseScreen(viewModel = viewModel)
                }
            }
        }
    }

    private fun solicitarPermisos() {
        // Solicitar permiso de notificaciones en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permiso ya concedido
                    viewModel.reprogramarTodosLosRecordatorios()
                }
                else -> {
                    requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // Para versiones anteriores a Android 13, los permisos ya están en el manifest
            viewModel.reprogramarTodosLosRecordatorios()
        }
    }
}