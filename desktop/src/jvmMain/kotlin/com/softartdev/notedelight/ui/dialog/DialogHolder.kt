package com.softartdev.notedelight.ui.dialog

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.di.AppModule
import com.softartdev.notedelight.ui.NoteDetailBody

@Stable
class DialogHolder {
    private var showDialog: Boolean by mutableStateOf(false)
    val dismissDialog = { showDialog = false }
    var dialogContent: @Composable () -> Unit = {}
    val showDialogIfNeed: @Composable () -> Unit = { if (showDialog) dialogContent() }

    private fun showDialog(content: @Composable () -> Unit) {
        dialogContent = content
        showDialog = true
    }

    fun showSaveChanges(saveNoteAndNavBack: () -> Unit, doNotSaveAndNavBack: () -> Unit) = showDialog {
        SaveDialog(saveNoteAndNavBack, doNotSaveAndNavBack, dismissDialog)
    }

    fun showEditTitle(noteId: Long, appModule: AppModule) = showDialog {
        EditTitleDialog(noteId, dismissDialog, appModule)
    }

    fun showDelete(onDeleteClick: () -> Unit) = showDialog {
        DeleteDialog(onDeleteClick, dismissDialog)
    }

    fun showError(message: String?) = showDialog {
        ErrorDialog(message, dismissDialog)
    }
}

@Composable
fun ErrorDialog(message: String?, dismissDialog: () -> Unit) = ShowDialog(
    title = MR.strings.error_title.localized(),
    text = message,
    onConfirm = dismissDialog,
    onDismiss = dismissDialog
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ShowDialog(title: String, text: String?, onConfirm: () -> Unit, onDismiss: () -> Unit) = AlertDialog(
    title = { Text(title) },
    text = text?.let { { Text(it) } },
    confirmButton = { Button(onClick = onConfirm) { Text(MR.strings.yes.localized()) } },
    dismissButton = { Button(onClick = onDismiss) { Text(MR.strings.cancel.localized()) } },
    onDismissRequest = onDismiss,
)

@Preview
@Composable
fun PreviewErrorDialog() = NoteDetailBody { ErrorDialog("preview err") {} }