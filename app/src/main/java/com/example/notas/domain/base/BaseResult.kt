package com.example.notas.domain.common

// Resultado de una operación: o ha ido bien (Success) o ha fallado (Error).
// "out T" permite usar un BaseResult<Note> donde se espera un BaseResult<Any>.
sealed class BaseResult<out T> {
    data class Success<T>(val data: T) : BaseResult<T>()
    data class Error(val exception: Exception) : BaseResult<Nothing>()
}
