package com.softartdev.notedelight.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.softartdev.mr.composeLocalized
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.shared.presentation.signin.SignInResult
import com.softartdev.notedelight.shared.presentation.signin.SignInViewModel
import com.softartdev.notedelight.ui.dialog.showError
import com.softartdev.themepref.DialogHolder
import com.softartdev.themepref.LocalThemePrefs

@Composable
fun SignInScreen(signInViewModel: SignInViewModel, navMain: () -> Unit) {
    val signInResultState: State<SignInResult> = signInViewModel.resultStateFlow.collectAsState()
    DisposableEffect(signInViewModel) {
        onDispose(signInViewModel::onCleared)
    }
    var label = MR.strings.enter_password.composeLocalized()
    var error by remember { mutableStateOf(false) }
    val passwordState: MutableState<String> = remember { mutableStateOf("") }
    val dialogHolder: DialogHolder = LocalThemePrefs.current.dialogHolder
    when (val signInResult: SignInResult = signInResultState.value) {
        is SignInResult.ShowSignInForm, is SignInResult.ShowProgress -> Unit
        is SignInResult.NavMain -> navMain()
        is SignInResult.ShowEmptyPassError -> {
            label = MR.strings.empty_password.composeLocalized()
            error = true
        }
        is SignInResult.ShowIncorrectPassError -> {
            label = MR.strings.incorrect_password.composeLocalized()
            error = true
        }
        is SignInResult.ShowError -> dialogHolder.showError(signInResult.error.message)
    }
    SignInScreenBody(
        showLoaing = signInResultState.value is SignInResult.ShowProgress,
        passwordState = passwordState,
        label = label,
        isError = error,
    ) { signInViewModel.signIn(pass = passwordState.value) }
}

@Composable
fun SignInScreenBody(
    showLoaing: Boolean = true,
    passwordState: MutableState<String> = mutableStateOf("password"),
    label: String = MR.strings.enter_password.composeLocalized(),
    isError: Boolean = false,
    onSignInClick: () -> Unit = {},
) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text(MR.strings.app_name.composeLocalized()) },
        )
    }
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(all = 16.dp)
    ) {
        Column {
            if (showLoaing) LinearProgressIndicator()
            PasswordField(
                modifier = Modifier.fillMaxWidth(),
                passwordState = passwordState,
                label = label,
                isError = isError,
                contentDescription = MR.strings.enter_password.composeLocalized()
            )
            Button(
                onClick = onSignInClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
            ) { Text(MR.strings.sign_in.composeLocalized()) }
        }
        LocalThemePrefs.current.showDialogIfNeed()
    }
}

@Preview
@Composable
fun PreviewSignInScreen() = SignInScreenBody()