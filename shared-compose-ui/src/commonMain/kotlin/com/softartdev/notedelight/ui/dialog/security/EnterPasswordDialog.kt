package com.softartdev.notedelight.ui.dialog.security

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.softartdev.mr.composeLocalized
import com.softartdev.mr.contextLocalized
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.shared.presentation.settings.security.enter.EnterResult
import com.softartdev.notedelight.shared.presentation.settings.security.enter.EnterViewModel
import com.softartdev.notedelight.ui.PasswordField
import com.softartdev.notedelight.ui.dialog.PreviewDialog
import com.softartdev.themepref.AlertDialog
import kotlinx.coroutines.launch

@Composable
fun EnterPasswordDialog(dismissDialog: () -> Unit, enterViewModel: EnterViewModel) {
    val enterResultState: State<EnterResult> = enterViewModel.resultStateFlow.collectAsState()
    DisposableEffect(enterViewModel) {
        onDispose(enterViewModel::onCleared)
    }
    var label = MR.strings.enter_password.composeLocalized()
    var error by remember { mutableStateOf(false) }
    val passwordState: MutableState<String> = remember { mutableStateOf("") }
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    when (val enterResult: EnterResult = enterResultState.value) {
        is EnterResult.InitState, is EnterResult.Loading -> Unit
        is EnterResult.Success -> dismissDialog()
        is EnterResult.EmptyPasswordError -> {
            label = MR.strings.empty_password.composeLocalized()
            error = true
        }
        is EnterResult.IncorrectPasswordError -> {
            label = MR.strings.incorrect_password.composeLocalized()
            error = true
        }
        is EnterResult.Error -> coroutineScope.launch {
            snackbarHostState.showSnackbar(enterResult.message ?: MR.strings.error_title.contextLocalized())
        }
    }
    ShowEnterPasswordDialog(
        showLoaing = enterResultState.value is EnterResult.Loading,
        passwordState = passwordState,
        label = label,
        isError = error,
        snackbarHostState = snackbarHostState,
        dismissDialog = dismissDialog
    ) { enterViewModel.enterCheck(password = passwordState.value) }
}

@Composable
fun ShowEnterPasswordDialog(
    showLoaing: Boolean = true,
    passwordState: MutableState<String> = mutableStateOf("password"),
    label: String = MR.strings.enter_password.composeLocalized(),
    isError: Boolean = true,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    dismissDialog: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
) = AlertDialog(
    title = { Text(text = MR.strings.dialog_title_enter_password.composeLocalized()) },
    text = {
        Column {
            if (showLoaing) LinearProgressIndicator()
            PasswordField(
                passwordState = passwordState,
                label = label,
                isError = isError,
                contentDescription = MR.strings.enter_password.composeLocalized(),
            )
            SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    },
    confirmButton = { Button(onClick = onConfirmClick) { Text(MR.strings.yes.composeLocalized()) } },
    dismissButton = { Button(onClick = dismissDialog) { Text(MR.strings.cancel.composeLocalized()) } },
    onDismissRequest = dismissDialog,
)

@Preview
@Composable
fun PreviewEnterPasswordDialog() = PreviewDialog { ShowEnterPasswordDialog() }