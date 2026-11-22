package com.example.notas.presentation.notes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notas.R
import com.example.notas.domain.common.BaseResult
import com.example.notas.domain.exception.NoteException
import com.example.notas.domain.model.Note
import com.example.notas.domain.repository.NoteRepository
import com.example.notas.domain.validation.TitleValidator
import com.example.notas.presentation.common.ProgressState
import com.example.notas.presentation.mapper.asStringRes
import kotlinx.coroutines.launch

class NotesViewModel(
    private val repository: NoteRepository   // recibe la INTERFAZ, no la implementación
) : ViewModel() {

    var uiState by mutableStateOf(AddNoteState())
        private set

    init {
        loadNotes()
    }

    // ---------- Eventos que llegan desde la UI ----------

    fun onTitleChange(title: String) {
        // Lógica de negocio ligera: el validador del dominio comprueba el título
        val error = TitleValidator.validate(title)
        uiState = uiState.copy(
            newTitle = title,
            titleError = error.asStringRes(),      // mapper: error → texto
            canAdd = !error.isError()
        )
    }

    fun onAddClick() {
        if (!uiState.canAdd) return

        viewModelScope.launch {
            // 1. Avisamos a la pantalla de que estamos guardando
            uiState = uiState.copy(progress = ProgressState.Loading, canAdd = false)

            // 2. Llamamos al repositorio (a la interfaz, no a la implementación)
            val note = Note(id = 0, title = uiState.newTitle.trim(), content = "")
            val result = repository.insert(note)

            // 3. Según el resultado, actualizamos el estado
            uiState = when (result) {
                is BaseResult.Success -> uiState.copy(
                    notes = repository.getNotes(),
                    newTitle = "",
                    progress = ProgressState.Idle,
                    isSaved = true                     // la pantalla mostrará un Toast
                )
                is BaseResult.Error -> uiState.copy(
                    progress = ProgressState.Idle,
                    // mapper: la excepción del repositorio se muestra como error del título
                    titleError = (result.exception as? NoteException)?.asStringRes()
                        ?: R.string.error_unknown
                )
            }
        }
    }

    // La pantalla avisa de que ya ha mostrado el Toast
    fun onToastShown() {
        uiState = uiState.copy(isSaved = false)
    }

    fun onDeleteClick(id: Int) {
        repository.delete(id)
        loadNotes()
    }

    // ---------- Privado ----------

    private fun loadNotes() {
        uiState = uiState.copy(notes = repository.getNotes())
    }
}
