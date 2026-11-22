package com.example.notas.presentation.mapper

import androidx.annotation.StringRes
import com.example.notas.R
import com.example.notas.domain.exception.NoteException

// Convierte una excepción de negocio del dominio en el id de un texto de strings.xml
@StringRes
fun NoteException.asStringRes(): Int = when (this) {
    is NoteException.DuplicateTitle -> R.string.error_duplicate_title
}
