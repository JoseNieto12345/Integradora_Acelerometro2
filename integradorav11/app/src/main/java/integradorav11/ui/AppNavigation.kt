package com.example.integradorav11.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.integradorav11.viewModel.HomeViewModelFactory // Asumiendo que esta existe
import com.example.integradorav11.viewModel.HistoryViewModelFactory // Asumiendo que esta existe

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