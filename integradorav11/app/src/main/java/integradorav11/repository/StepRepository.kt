package com.example.integradorav11.repository

import kotlin.Result

import com.example.integradorav11.remote.StepApiService
import com.example.integradorav11.model.StepEntry
import com.example.integradorav11.model.Result

// repository/StepRepository.kt
class StepRepository(private val apiService: StepApiService) {

    // Manejo de GET (Historial)
    suspend fun getHistory(): kotlin.Result<List<StepEntry>> {
        return try {
            val steps = apiService.getHistorySteps()
            kotlin.Result.Success(steps)
        } catch (e: Exception) {
            // Manejo de errores de conexión/HTTP
            kotlin.Result.Failure(e)
        }
    }

    // Manejo de POST
    suspend fun saveSteps(steps: Int, date: String): kotlin.Result<Unit> {
        return try {
            val response = apiService.postSteps(StepEntry(date = date, steps = steps))

            if (response.isSuccessful) {
                kotlin.Result.Success(Unit)
            } else {
                // Manejar códigos de respuesta específicos (4xx, 5xx)
                kotlin.Result.Failure(Exception("Error al guardar: ${response.code()}"))
            }
        } catch (e: Exception) {
            kotlin.Result.Failure(e)
        }
    }

    // Manejo de DELETE
    suspend fun deleteSteps(date: String): kotlin.Result<Unit> {
        return try {
            val response = apiService.deleteSteps(date)
            if (response.isSuccessful) {
                kotlin.Result.Success(Unit)
            } else {
                kotlin.Result.Failure(Exception("Error al borrar: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}
