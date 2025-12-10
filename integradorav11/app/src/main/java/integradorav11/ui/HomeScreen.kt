package com.example.integradorav11.ui



@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToHistory: () -> Unit
) {
    val steps by viewModel.stepsToday.collectAsState(initial = 0)
    val uiMessage by viewModel.uiMessage.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally, // <-- USO CORREGIDO
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

        // Muestra mensajes de error o éxito
        uiMessage?.let {
            Snackbar(
                modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally), // Centrar el Snackbar
                action = { Button(onClick = { viewModel.messageConsumed() }) { Text("OK") } }
            ) { Text(it) }
        }

        // Botón de navegación
        Button(
            onClick = onNavigateToHistory,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver Historial de Pasos")
        }
    }
}