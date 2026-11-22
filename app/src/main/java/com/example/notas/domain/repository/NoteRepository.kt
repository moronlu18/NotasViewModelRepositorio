package com.example.notas.domain.repository

import com.example.notas.domain.common.BaseResult
import com.example.notas.domain.model.Note

// El «contrato»: QUÉ se puede hacer con las notas, sin decir CÓMO
interface NoteRepository {
    fun getNotes(): List<Note>

    // suspend: como un servidor o una base de datos, tarda un poco en responder
    suspend fun insert(note: Note): BaseResult<Note>

    fun delete(id: Int)
}
