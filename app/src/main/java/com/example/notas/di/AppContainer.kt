package com.example.notas.di

import com.example.notas.data.repository.InMemoryNoteRepository
import com.example.notas.domain.repository.NoteRepository

// Un único repositorio para toda la app. Es inyección de dependencias «manual»;
// más adelante esto lo hará Hilt.
object AppContainer {
    // Para usar otra implementación (por ejemplo, con Room) solo hay que cambiar esta línea
    val noteRepository: NoteRepository by lazy { InMemoryNoteRepository() }
}
