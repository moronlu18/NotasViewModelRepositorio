package com.example.notas.domain.exception

// Errores de negocio de las notas. El mensaje es técnico: solo para Logcat.
// El texto que ve el usuario lo decide la capa de presentación (mapper).
sealed class NoteException(message: String) : Exception(message) {
    class DuplicateTitle(val title: String) : NoteException("Ya existe una nota con el título $title")
}
