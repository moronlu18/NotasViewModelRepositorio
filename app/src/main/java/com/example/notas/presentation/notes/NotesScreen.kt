package com.example.notas.presentation.notes

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notas.R
import com.example.notas.domain.model.Note
import com.example.notas.presentation.common.ProgressState

// Composable con estado: recibe el ViewModel desde MainActivity y decide qué hacer con el estado
@Composable
fun NotesScreen(
    viewModel: NotesViewModel,
    modifier: Modifier = Modifier
) {
    val state = viewModel.uiState
    val context = LocalContext.current

    // Cuando la nota se ha guardado, se muestra un Toast y se avisa al ViewModel
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            Toast.makeText(context, context.getString(R.string.note_added), Toast.LENGTH_SHORT).show()
            viewModel.onToastShown()
        }
    }

    NotesContent(
        state = state,
        onTitleChange = viewModel::onTitleChange,
        onAddClick = viewModel::onAddClick,
        onDeleteClick = viewModel::onDeleteClick,
        modifier = modifier
    )
}

// Composable sin estado: solo pinta lo que recibe en AddNoteState
@Composable
fun NotesContent(
    state: AddNoteState,
    onTitleChange: (String) -> Unit,
    onAddClick: () -> Unit,
    onDeleteClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = state.newTitle,
                onValueChange = onTitleChange,
                label = { Text(stringResource(R.string.new_note)) },
                isError = state.titleError != null,
                supportingText = { state.titleError?.let { Text(stringResource(it)) } },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = onAddClick, enabled = state.canAdd) {
                Text(stringResource(R.string.add))
            }
        }

        if (state.progress == ProgressState.Loading) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.saving))
            }
        }

        Spacer(Modifier.height(16.dp))

        if (state.notes.isEmpty()) {
            Text(stringResource(R.string.no_notes))
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.notes, key = { it.id }) { note ->
                Card(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(note.title, Modifier.weight(1f))
                        IconButton(onClick = { onDeleteClick(note.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
                        }
                    }
                }
            }
        }
    }
}

// Como NotesContent no tiene estado, se puede previsualizar cualquier caso, también el error
@Preview(showBackground = true)
@Composable
fun NotesContentErrorPreview() {
    NotesContent(
        state = AddNoteState(
            notes = listOf(Note(1, "Bienvenida", "")),
            newTitle = "Bienvenida",
            titleError = R.string.error_duplicate_title
        ),
        onTitleChange = {}, onAddClick = {}, onDeleteClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun NotesContentLoadingPreview() {
    NotesContent(
        state = AddNoteState(
            notes = listOf(Note(1, "Bienvenida", "")),
            newTitle = "Comprar pan",
            progress = ProgressState.Loading
        ),
        onTitleChange = {}, onAddClick = {}, onDeleteClick = {}
    )
}
