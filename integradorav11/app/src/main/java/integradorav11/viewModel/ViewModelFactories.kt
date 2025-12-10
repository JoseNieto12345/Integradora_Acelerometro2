package com.example.integradorav11.viewModel


// viewmodel/ViewModelFactories.kt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.integradorav11.viewModel.HistoryViewModel
import com.example.integradorav11.viewModel.HomeViewModel
import com.example.integradorav11.remote.RetrofitClient
import com.example.integradorav11.repository.StepRepository

// Inicializamos el Repositorio una sola vez
private val repository = StepRepository(RetrofitClient.stepApiService)

// Factoría para HomeViewModel
val HomeViewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: HomeViewModel")
    }
}

// Factoría para HistoryViewModel
val HistoryViewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: HistoryViewModel")
    }
}