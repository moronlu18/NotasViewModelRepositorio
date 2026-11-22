package com.example.notas.presentation.common

// Estado de una operación que tarda (guardar, cargar…). Es común para todas las pantallas.
// Con una sealed interface es imposible estar "en reposo" y "cargando" a la vez.
sealed interface ProgressState {
    data object Idle : ProgressState
    data object Loading : ProgressState
}
