package com.example.integradorav11.viewModel


// viewmodel/HistoryViewModel.kt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradorav11.model.StepEntry // Se asume que StepEntry existe en este paquete
import com.example.integradorav11.repository.StepRepository
import com.example.integradorav11.model.Result // <-- IMPORTACIÓN NECESARIA
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

class HistoryViewModel(private val repository: StepRepository) : ViewModel() {

    // Estado de la lista de historial
    private val _historySteps = MutableStateFlow<List<StepEntry>>(emptyList())
    val historySteps: StateFlow<List<StepEntry>> = _historySteps.asStateFlow()

    // Estado para saber si está cargando
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Estado para mensajes de la UI (errores/éxito)
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    init {
        // Cargar el historial tan pronto como se crea el ViewModel
        loadHistory()
    }

    // OPERACIÓN GET
    fun loadHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Se corrigió el error: la clase Result ahora está disponible via importación
                when (val result = repository.getHistory()) {
                    is Result.Success -> _historySteps.value = result.data.sortedByDescending { it.date }
                    is Result.Failure -> _uiMessage.value = "Error al cargar historial: ${result.exception.message}" // USO CORREGIDO
                }
            } catch (e: Exception) {
                // Manejar excepción de cancelación de corutina
                if (e is CancellationException) throw e
                _uiMessage.value = "Error de conexión o desconocido."
            } finally {
                _isLoading.value = false
            }
        }
    }

    // OPERACIÓN DELETE
    fun deleteStepEntry(date: String) {
        viewModelScope.launch {
            _isLoading.value = true
            // Se corrigió el error: la clase Result ahora está disponible via importación
            when (val result = repository.deleteSteps(date)) {
                is Result.Success -> {
                    _uiMessage.value = "Registro del $date borrado con éxito."
                    // Recargar el historial para reflejar el cambio
                    loadHistory()
                }
                is Result.Failure -> {
                    _uiMessage.value = "Error al borrar el registro: ${result.exception.message}" // USO CORREGIDO
                    _isLoading.value = false
                }
            }
        }
    }

    fun messageConsumed() {
        _uiMessage.value = null // Limpiar el mensaje de error/éxito
    }
}
