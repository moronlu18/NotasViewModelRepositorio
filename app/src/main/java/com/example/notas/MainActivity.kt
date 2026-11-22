package com.example.notas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.notas.di.AppContainer
import com.example.notas.presentation.notes.NotesScreen
import com.example.notas.presentation.notes.NotesViewModel

// La actividad crea el ViewModel y se lo pasa a la pantalla: sin Factory
class MainActivity : ComponentActivity() {
    private val viewModel = NotesViewModel(AppContainer.noteRepository)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    NotesScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
