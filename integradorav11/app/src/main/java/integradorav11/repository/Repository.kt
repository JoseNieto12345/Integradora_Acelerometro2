package com.example.integradorav11.repository


/**
 * Clase sellada utilizada para representar el resultado de operaciones
 * asíncronas (como la interacción con Firebase), que pueden ser un éxito (Success)
 * o un fallo (Failure).
 */
sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Failure(val exception: Exception) : Result<Nothing>()
}