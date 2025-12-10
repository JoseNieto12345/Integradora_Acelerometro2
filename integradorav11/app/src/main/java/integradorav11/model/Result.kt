package com.example.integradorav11.model

// model/Result.kt
sealed class Result<out T> {
    // 1. Éxito: Contiene el dato que esperábamos (ej. List<StepEntry>)
    data class Success<out T>(val data: T) : Result<T>()

    // 2. Fallo: Contiene la excepción o error que ocurrió (ej. SocketTimeoutException, Error HTTP 404)
    data class Failure(val exception: Throwable) : Result<Nothing>()

    // Opcionalmente, puedes añadir Loading
    // object Loading : Result<Nothing>()
}