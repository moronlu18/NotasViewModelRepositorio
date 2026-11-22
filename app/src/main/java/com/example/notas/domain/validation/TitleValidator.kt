package com.example.notas.domain.validation

// Valida el título de una nota. Solo dice QUÉ error hay; el texto lo pone el mapper.
object TitleValidator {
    const val MAX_LENGTH = 50

    fun validate(title: String): TitleError = when {
        title.isBlank() -> TitleError.Empty
        title.length > MAX_LENGTH -> TitleError.TooLong
        else -> TitleError.None
    }
}
