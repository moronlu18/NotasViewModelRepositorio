package com.example.notas.domain.model

// Entidad del dominio: qué es una nota para nuestra app (Kotlin puro)
data class Note(
    val id: Int,
    val title: String,
    val content: String
)
