package com.example.integradorav11.ui



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: HistoryViewModel = viewModel(factory = HistoryViewModelFactory)) {

    val stepsHistory by viewModel.historySteps.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Historial de Pasos") }) },
        snackbarHost = {
            SnackbarHost(hostState = remember { SnackbarHostState() })
        }
    ) { paddingValues ->

        // Manejo de la carga
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        // Manejo de mensajes de error/éxito
        uiMessage?.let { message ->
            LaunchedEffect(message) {
                // Mostrar el Snackbar con el mensaje y luego notificar al ViewModel para limpiar el estado
                SnackbarHostState().showSnackbar(
                    message = message,
                    actionLabel = "Cerrar"
                )
                viewModel.messageConsumed()
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 8.dp)
        ) {
            item {
                if (stepsHistory.isEmpty() && !isLoading) {
                    Text(
                        text = "No hay registros de pasos anteriores.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            items(stepsHistory) { entry ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(entry.date, style = MaterialTheme.typography.titleMedium)
                            Text("${entry.steps} pasos", style = MaterialTheme.typography.headlineLarge)
                        }

                        // Botón de DELETE
                        IconButton(
                            onClick = {
                                // Llamar a la operación DELETE en el ViewModel
                                viewModel.deleteStepEntry(entry.date)
                            },
                            enabled = !isLoading // Deshabilitar durante la carga
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Borrar registro",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}