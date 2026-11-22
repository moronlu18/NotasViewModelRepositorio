package com.example.notas.presentation.mapper

import androidx.annotation.StringRes
import com.example.notas.R
import com.example.notas.domain.validation.TitleError

// Convierte un error de validación del dominio en el id de un texto de strings.xml.
// Devuelve null si no hay error.
@StringRes
fun TitleError.asStringRes(): Int? = when (this) {
    TitleError.Empty -> R.string.title_error_empty
    TitleError.TooLong -> R.string.title_error_too_long
    TitleError.None -> null          // sin error, no hay texto
}
