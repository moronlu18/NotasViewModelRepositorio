package com.example.notas.data.repository

import com.example.notas.domain.common.BaseResult
import com.example.notas.domain.exception.NoteException
import com.example.notas.domain.model.Note
import com.example.notas.domain.repository.NoteRepository
import kotlinx.coroutines.delay

// Implementación del repositorio que guarda las notas en memoria
class InMemoryNoteRepository : NoteRepository {

    // Simulamos los datos que vendrían de una base de datos o de un servidor
    private val notes = mutableListOf(Note(id = 1, title = "Bienvenida", content = ""))
    private var nextId = 2

    // Devuelve una copia: nadie puede modificar la lista original
    override fun getNotes(): List<Note> = notes.toList()

    override suspend fun insert(note: Note): BaseResult<Note> {
        delay(1500) // simulamos que el servidor o la base de datos tarda en responder

        // Regla de negocio: no puede haber dos notas con el mismo título
        val repeated = notes.any { it.title.equals(note.title.trim(), ignoreCase = true) }
        if (repeated) {
            return BaseResult.Error(NoteException.DuplicateTitle(note.title))
        }

        // El repositorio asigna el id
        val newNote = note.copy(id = nextId++)
        notes.add(newNote)
        return BaseResult.Success(newNote)
    }

    override fun delete(id: Int) {
        notes.removeAll { it.id == id }
    }
}
