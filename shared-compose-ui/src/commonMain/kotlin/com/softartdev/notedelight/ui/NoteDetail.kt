package com.softartdev.notedelight.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.softartdev.annotation.Preview
import com.softartdev.mr.composeLocalized
import com.softartdev.mr.contextLocalized
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.shared.presentation.note.NoteResult
import com.softartdev.notedelight.shared.presentation.note.NoteViewModel
import com.softartdev.notedelight.ui.dialog.showDelete
import com.softartdev.notedelight.ui.dialog.showEditTitle
import com.softartdev.notedelight.ui.dialog.showError
import com.softartdev.notedelight.ui.dialog.showSaveChanges
import com.softartdev.themepref.DialogHolder
import com.softartdev.themepref.LocalThemePrefs
import kotlinx.coroutines.launch

@Composable
fun NoteDetail(
    noteId: Long,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    noteViewModel: NoteViewModel,
) {
    val noteResultState: State<NoteResult> = noteViewModel.resultStateFlow.collectAsState()
    DisposableEffect(noteId) {
        when (noteId) {
            0L -> noteViewModel.createNote()
            else -> noteViewModel.loadNote(noteId)
        }
        onDispose(noteViewModel::onCleared)
    }
    val titleState: MutableState<String> = remember { mutableStateOf("") }
    val textState: MutableState<String> = remember { mutableStateOf("") }

    val dialogHolder: DialogHolder = LocalThemePrefs.current.dialogHolder

    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val snackbarHostState: SnackbarHostState = scaffoldState.snackbarHostState
    val coroutineScope = rememberCoroutineScope()
    when (val noteResult: NoteResult = noteResultState.value) {
        is NoteResult.Loading,
        is NoteResult.Created -> Unit
        is NoteResult.Loaded -> {
            titleState.value = noteResult.result.title
            textState.value = noteResult.result.text
        }
        is NoteResult.Saved -> coroutineScope.launch {
            titleState.value = noteResult.title
            val noteSaved = MR.strings.note_saved.contextLocalized() + ": " + noteResult.title
            snackbarHostState.showSnackbar(noteSaved)
        }
        is NoteResult.NavEditTitle -> dialogHolder.showEditTitle(noteId)
        is NoteResult.TitleUpdated -> {
            titleState.value = noteResult.title
        }
        is NoteResult.Empty -> coroutineScope.launch {
            snackbarHostState.showSnackbar(MR.strings.note_empty.contextLocalized())
        }
        is NoteResult.Deleted -> coroutineScope.launch {
            dialogHolder.dismissDialog()
            onBackClick()
            snackbarHostState.showSnackbar(MR.strings.note_deleted.contextLocalized())
        }
        is NoteResult.CheckSaveChange -> dialogHolder.showSaveChanges(
            saveNoteAndNavBack = { noteViewModel.saveNoteAndNavBack(titleState.value, textState.value) },
            doNotSaveAndNavBack = noteViewModel::doNotSaveAndNavBack,
        )
        is NoteResult.NavBack -> onBackClick()
        is NoteResult.Error -> dialogHolder.showError(noteResult.message)
    }
    NoteDetailBody(
        scaffoldState = scaffoldState,
        titleState = titleState,
        textState = textState,
        onBackClick = { noteViewModel.checkSaveChange(titleState.value, textState.value) },
        onSaveClick = noteViewModel::saveNote,
        onEditClick = noteViewModel::editTitle,
        onDeleteClick = { dialogHolder.showDelete(onDeleteClick = noteViewModel::deleteNote) },
        onSettingsClick = onSettingsClick,
        showLoaing = noteResultState.value == NoteResult.Loading,
    )
}

@Composable
fun NoteDetailBody(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    titleState: MutableState<String> = mutableStateOf("Title"),
    textState: MutableState<String> = mutableStateOf("Text"),
    onBackClick: () -> Unit = {},
    onSaveClick: (title: String?, text: String) -> Unit = { _, _ -> },
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    showLoaing: Boolean = true,
) = Scaffold(
    scaffoldState = scaffoldState,
    topBar = {
        TopAppBar(
            title = { Text(text = titleState.value, maxLines = 1) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
            },
            actions = {
                IconButton(onClick = { onSaveClick(titleState.value, textState.value) }) {
                    Icon(Icons.Default.Save, contentDescription = MR.strings.action_save_note.composeLocalized())
                }
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Title, contentDescription = MR.strings.action_edit_title.composeLocalized())
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = MR.strings.action_delete_note.composeLocalized())
                }
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Settings, contentDescription = MR.strings.settings.composeLocalized())
                }
            }
        )
    }) {
    Box {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (showLoaing) LinearProgressIndicator()
            TextField(
                value = textState.value,
                onValueChange = { textState.value = it },
                modifier = Modifier.weight(1F).fillMaxWidth().padding(8.dp),
                label = { Text(MR.strings.type_text.composeLocalized()) },
            )
        }
        LocalThemePrefs.current.showDialogIfNeed()
    }
}

@Preview
@Composable
fun PreviewNoteDetailBody() = NoteDetailBody()