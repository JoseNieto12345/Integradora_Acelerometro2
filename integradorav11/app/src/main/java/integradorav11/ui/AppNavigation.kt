package com.example.integradorav11.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.integradorav11.viewModel.HomeViewModelFactory
import com.example.integradorav11.viewModel.HistoryViewModelFactory

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(
                viewModel = viewModel(factory = HomeViewModelFactory),
                onNavigateToHistory = {
                    navController.navigate("history")
                }
            )
        }

        composable("history") {
            HistoryScreen(
                viewModel = viewModel(factory = HistoryViewModelFactory),
                onNavigateBack = {
                    navController.popBackStack() // Acción para volver atrás
                }
            )
        }
    }
}