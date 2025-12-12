package com.example.integradorav11.viewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradorav11.repository.StepRepository
import com.example.integradorav11.service.StepCounterService
import com.example.integradorav11.model.Result // Importación de la clase sellada Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date // <-- Importación para obtener la fecha
import java.util.Locale // <-- Importación  para el formato de fecha

class HomeViewModel(private val repository: StepRepository) : ViewModel() {

    // Estado del contador de pasos de hoy (viene del SensorService)
    val stepsToday = StepCounterService.stepsFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0
    )

    // Estado de la UI para mensajes (errores o éxito)
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    // Llamada para guardar los pasos del día (POST)
    fun saveTodaySteps() {
        // Se utiliza java.util.Date y java.util.Locale
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentSteps = stepsToday.value

        viewModelScope.launch {
            _uiMessage.value = "Guardando pasos..."
            when (val result = repository.saveSteps(currentSteps, todayDate)) {
                is Result.Success -> _uiMessage.value = "Pasos guardados exitosamente."
                is Result.Failure -> _uiMessage.value = "Error al guardar: ${result.exception.message}" // <-- Sintaxis corregida
            }
        }
    }

    fun messageConsumed() {
        _uiMessage.value = null // Limpiar el mensaje después de mostrarlo en la UI
    }
}