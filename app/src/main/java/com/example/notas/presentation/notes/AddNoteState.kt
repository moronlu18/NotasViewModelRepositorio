package com.example.notas.presentation.notes

import androidx.annotation.StringRes
import com.example.notas.domain.model.Note
import com.example.notas.presentation.common.ProgressState

// Estado de la pantalla. Solo lo modifica el ViewModel, con copy().
data class AddNoteState(
    val notes: List<Note> = emptyList(),
    val newTitle: String = "",
    @StringRes val titleError: Int? = null,   // error del título (validación o repositorio), o null
    val canAdd: Boolean = false,
    val progress: ProgressState = ProgressState.Idle,
    val isSaved: Boolean = false               // true cuando la nota se ha guardado: la pantalla muestra un Toast
)
