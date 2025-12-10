package com.example.integradorav11.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.integradorav11.viewModel.HomeViewModel


@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToHistory: () -> Unit
) {
    val steps by viewModel.stepsToday.collectAsState(initial = 0)
    val uiMessage by viewModel.uiMessage.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Bloque superior: Pasos y botón POST
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Pasos Dados Hoy", style = MaterialTheme.typography.headlineMedium)
            Text(
                text = steps.toString(),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(32.dp))
            Button(onClick = {
                viewModel.saveTodaySteps() // Operación POST
            }) {
                Text("Guardar Pasos en Servidor (POST)")
            }
        }

        // Bloque inferior: Snackbar y Botón de Navegación
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Muestra mensajes de error o éxito
            uiMessage?.let {
                Snackbar(
                    modifier = Modifier.fillMaxWidth(),
                    action = { Button(onClick = { viewModel.messageConsumed() }) { Text("OK") } }
                ) { Text(it) }
            }

            Spacer(Modifier.height(8.dp))

            // Botón de navegación
            Button(
                onClick = onNavigateToHistory,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver Historial de Pasos")
            }
        }
    }
}