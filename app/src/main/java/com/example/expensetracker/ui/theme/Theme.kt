package com.example.medicacionapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val EsquemaColorOscuro = darkColorScheme(
    primary = AzulMedicoClaro,
    secondary = VerdeMedicinaClaro,
    tertiary = RojoAlertaClaro,
    background = FondoOscuro,
    surface = FondoTarjetaOscura,
    onPrimary = TextoBlanco,
    onSecondary = TextoBlanco,
    onTertiary = TextoBlanco,
    onBackground = TextoBlanco,
    onSurface = TextoBlanco,
)

private val EsquemaColorClaro = lightColorScheme(
    primary = AzulMedico,
    secondary = VerdeMedicina,
    tertiary = RojoAlerta,
    background = FondoClaro,
    surface = FondoTarjeta,
    onPrimary = TextoBlanco,
    onSecondary = TextoBlanco,
    onTertiary = TextoBlanco,
    onBackground = TextoPrincipal,
    onSurface = TextoPrincipal,
)

@Composable
fun ExpenseTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> EsquemaColorOscuro
        else -> EsquemaColorClaro
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}