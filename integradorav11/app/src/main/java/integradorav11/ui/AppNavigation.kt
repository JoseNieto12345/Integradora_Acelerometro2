package com.example.integradorav11.ui



@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            // **Punto Clave:** Pasar la función onNavigateToHistory a HomeScreen
            HomeScreen(
                viewModel = viewModel(factory = HomeViewModelFactory),
                onNavigateToHistory = {
                    // Ejecuta la navegación cuando se llama desde HomeScreen
                    navController.navigate("history")
                }
            )
        }

        composable("history") {
            // Usar HistoryViewModelFactory para la pantalla de historial
            HistoryScreen(
                viewModel = viewModel(factory = HistoryViewModelFactory)
            )
        }
    }
}