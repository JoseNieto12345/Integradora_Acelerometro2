package com.example.integradorav11.viewModel


// viewmodel/HistoryViewModel.kt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradorav11.model.StepEntry
import com.example.integradorav11.repository.StepRepository
import com.example.integradorav11.model.Result
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
                when (val result = repository.getHistory()) {
                    is Result.Success -> _historySteps.value = result.data.sortedByDescending { it.date }
                    is Result.Failure -> _uiMessage.value = "Error al cargar historial: ${result.exception.message}"
                }
            } catch (e: Exception) {
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
            when (val result = repository.deleteSteps(date)) {
                is Result.Success -> {
                    _uiMessage.value = "Registro del $date borrado con éxito."
                    // Recargar el historial para reflejar el cambio
                    loadHistory()
                }
                is Result.Failure -> {
                    _uiMessage.value = "Error al borrar el registro: ${result.exception.message}"
                    _isLoading.value = false
                }
            }
        }
    }

    // OPERACIÓN UPDATE (Reutiliza saveSteps que hace un Upsert)
    fun updateStepEntry(date: String, newSteps: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repository.saveSteps(newSteps, date)) {
                is Result.Success -> {
                    _uiMessage.value = "Registro del $date actualizado a $newSteps pasos."
                    loadHistory() // Recargar para mostrar los cambios
                }
                is Result.Failure -> {
                    _uiMessage.value = "Error al actualizar: ${result.exception.message}"
                    _isLoading.value = false
                }
            }
        }
    }

    fun messageConsumed() {
        _uiMessage.value = null
    }
}