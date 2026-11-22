# Notas: ViewModel y Repositorio

Proyecto de ejemplo de la lección **ViewModel y Repositorio** del curso *DEINT Jetpack Compose* (moronlu18.com).

App de notas: se escribe un título, se pulsa **Añadir** y la nota aparece en la lista. Al guardar se muestra el progreso y, al terminar, un Toast «Nota añadida». Si el título está vacío, es demasiado largo o ya existe, el error aparece debajo del campo. Las notas también se pueden borrar con el icono de papelera.

## Qué se practica

- **ViewModel** con el estado de la pantalla (`AddNoteState`) en `mutableStateOf` y `private set`.
- **Repositorio**: interfaz en el dominio (`NoteRepository`) e implementación en memoria (`InMemoryNoteRepository`) que simula con `delay()` un servidor o una base de datos.
- **BaseResult**: el repositorio devuelve `Success` o `Error` en lugar de lanzar excepciones.
- **NoteException**: errores de negocio del dominio (título repetido).
- **Validador**: `TitleValidator` comprueba el título (lógica de negocio ligera) y devuelve un `TitleError`.
- **Mappers**: convierten `TitleError` y `NoteException` en textos de `strings.xml` (`@StringRes`).
- **ProgressState**: estado común para operaciones que tardan (`Idle`, `Loading`).
- **State hoisting**: `NotesScreen` tiene el estado y `NotesContent` solo pinta (tiene dos `@Preview`: error y cargando).
- **Inyección manual**: `AppContainer` expone el repositorio; más adelante lo hará Hilt.

No se usan casos de uso: la regla «no puede haber dos notas con el mismo título» la comprueba el repositorio.

## Partes del proyecto

### Nivel superior

```
.
├── app/                       // Módulo Android: código Kotlin y recursos
├── build.gradle.kts           // Plugins declarados (se aplican en los módulos)
├── settings.gradle.kts        // Nombre del proyecto, repositorios y módulos
├── gradle.properties          // Opciones de Gradle
├── gradle/libs.versions.toml  // Catálogo de versiones y dependencias
├── gradlew / gradlew.bat      // Wrappers de Gradle (Linux/Windows)
├── gradle/wrapper/            // JAR y propiedades del wrapper
├── local.properties           // Ruta al SDK (local, ignorada por Git)
├── .gitignore                 // Excluye build/, .gradle/, .idea/, etc.
└── README.md                  // Estos apuntes
```

### Módulo `app`

```
app/
├── build.gradle.kts           // SDK 35, minSdk 24, Compose, Java 17, lifecycle-viewmodel-compose
├── proguard-rules.pro         // Reglas de ofuscación (release)
└── src/main/
    ├── AndroidManifest.xml    // Declara MainActivity como launcher
    ├── java/com/example/notas/
    │   ├── MainActivity.kt    // Solo setContent { NotesScreen() }
    │   ├── domain/            // Kotlin puro: model, repository, validation, exception, base
    │   ├── data/              // Implementación del repositorio en memoria
    │   ├── di/                // AppContainer (inyección manual)
    │   └── presentation/      // ViewModel, estado, mappers y Compose
    └── res/values/
        ├── strings.xml        // Todos los textos de la interfaz
        └── themes.xml         // Tema Material (Theme.Notas)
```

### Código por capas

```
com/example/notas/
├── MainActivity.kt
├── domain/
│   ├── model/Note.kt                    // Entidad (id, title, content)
│   ├── base/BaseResult.kt               // Success / Error  (paquete ...domain.common)
│   ├── exception/NoteException.kt       // DuplicateTitle
│   ├── validation/TitleError.kt         // Empty / TooLong / None
│   ├── validation/TitleValidator.kt     // MAX_LENGTH = 50
│   └── repository/NoteRepository.kt     // Contrato: getNotes, insert, delete
├── data/
│   └── repository/InMemoryNoteRepository.kt  // Lista en memoria + delay(1500)
├── di/
│   └── AppContainer.kt                  // Única instancia del repositorio
└── presentation/
    ├── base/ProgressState.kt            // Idle / Loading  (paquete ...presentation.common)
    ├── mapper/TitleErrorMapper.kt       // TitleError → @StringRes
    ├── mapper/NoteExceptionMapper.kt    // NoteException → @StringRes
    └── notes/
        ├── AddNoteState.kt              // Estado de la pantalla
        ├── NotesViewModel.kt            // Eventos y lógica de presentación
        └── NotesScreen.kt               // NotesScreen (stateful) + NotesContent (stateless)
```

> Las carpetas `base/` contienen los paquetes `...domain.common` y
> `...presentation.common`. En Kotlin el nombre del directorio no tiene por qué
> coincidir con el paquete, aunque aquí conviene tenerlo presente al buscar un archivo.

## Recorrido de una acción

1. `NotesContent` avisa con `onAddClick`; `NotesScreen` lo delega a `viewModel.onAddClick()`.
2. El ViewModel pone `progress = Loading` y `canAdd = false`.
3. Crea la nota y llama a `repository.insert(note)` (a la **interfaz**, no a la implementación).
4. `InMemoryNoteRepository` espera 1,5 s, comprueba que el título no esté repetido y devuelve `BaseResult.Success` o `BaseResult.Error(DuplicateTitle)`.
5. El ViewModel actualiza `uiState`: lista nueva y `isSaved = true`, o `titleError` con el texto del mapper.
6. `NotesContent` vuelve a pintar; `NotesScreen` muestra el Toast cuando `isSaved` es `true` y avisa con `onToastShown()`.

## Actualizar (editar) una nota

La app solo **añade** y **borra** notas: no hay pantalla de edición. La ruta del
código para implementar la actualización sería, de dentro hacia fuera:

1. `domain/repository/NoteRepository.kt`: añadir `suspend fun update(note: Note): BaseResult<Note>`.
2. `data/repository/InMemoryNoteRepository.kt`: implementarla buscando la nota por `id` y sustituyéndola en la lista (`notes[index] = note`), devolviendo `Success` o un `NoteException` nuevo si no existe.
3. `presentation/notes/NotesViewModel.kt`: añadir un evento `onUpdateClick(id, title)` que valide con `TitleValidator`, llame a `repository.update(...)` y actualice `uiState`.
4. `presentation/notes/AddNoteState.kt`: añadir el estado necesario (nota en edición, errores, progreso).
5. `presentation/notes/NotesScreen.kt`: permitir pulsar una nota para editarla y mostrar el formulario en modo edición.

Los apuntes (este README) se actualizan en la ruta local del proyecto y se publican con `git add README.md && git commit -m "docs: ..." && git push`.

## Para probarlo

1. Escribe «Comprar pan» y pulsa **Añadir**: verás «Guardando…» durante 1,5 segundos y después el Toast.
2. Escribe «Bienvenida» (ya existe) y pulsa **Añadir**: el repositorio devuelve un error y aparece «Ya existe una nota con ese título» debajo del campo.
3. Borra el título: aparece «El título no puede estar vacío» y el botón se desactiva.
4. Pulsa la papelera de una nota: desaparece de la lista.

Pon puntos de ruptura en `onTitleChange`, `onAddClick` e `InMemoryNoteRepository.insert` para seguir el recorrido con el depurador.

## Cómo abrirlo

Abre la carpeta con Android Studio, espera a que termine la sincronización de Gradle y ejecuta la app en un emulador o en un móvil (Android 7.0 o superior).
