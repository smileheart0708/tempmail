package com.temp.mail.ui.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun ShowSnackbar(
    snackBarHostState: SnackbarHostState,
    message: String,
    onDismiss: () -> Unit
) {
    LaunchedEffect(message) {
        if (message.isNotEmpty()) {
            snackBarHostState.showSnackbar(message)
            onDismiss()
        }
    }
}