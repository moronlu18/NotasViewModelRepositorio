package com.example.notas.domain.validation

// Posibles errores al validar el título de una nota (Kotlin puro, sin textos)
sealed class TitleError {
    data object Empty : TitleError()
    data object TooLong : TitleError()
    data object None : TitleError()

    fun isError(): Boolean = this !is None
}
