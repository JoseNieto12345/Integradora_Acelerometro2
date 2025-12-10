package com.example.integradorav11.repository

import com.example.integradorav11.remote.StepApiService
import com.example.integradorav11.model.StepEntry
import com.example.integradorav11.model.Result

// repository/StepRepository.kt
class StepRepository(private val apiService: StepApiService) {

    // Manejo de GET (Historial)
    suspend fun getHistory(): Result<List<StepEntry>> {
        return try {
            val steps = apiService.getHistorySteps()
            Result.Success(steps)
        } catch (e: Exception) {
            // Manejo de errores de conexión/HTTP
            Result.Failure(e)
        }
    }

    // Manejo de POST
    suspend fun saveSteps(steps: Int, date: String): Result<Unit> {
        return try {
            // Se modificó para enviar los campos individuales (date, steps) como form-url-encoded
            // en lugar de un objeto JSON completo en el cuerpo.
            val response = apiService.postSteps(date, steps)

            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                // Manejar códigos de respuesta específicos (4xx, 5xx)
                Result.Failure(Exception("Error al guardar: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    // Manejo de DELETE
    suspend fun deleteSteps(date: String): Result<Unit> {
        return try {
            val response = apiService.deleteSteps(date)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Failure(Exception("Error al borrar: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}