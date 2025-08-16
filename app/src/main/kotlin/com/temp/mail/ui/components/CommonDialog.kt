package com.temp.mail.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import android.content.ClipData
import android.content.ClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.temp.mail.R
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CommonDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    title: String,
    text: String,
    isError: Boolean = false,
) {
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(ClipboardManager::class.java)
    val scope = rememberCoroutineScope()

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            icon = if (isError) {
                { Icon(Icons.Default.Warning, contentDescription = "Error Icon") }
            } else {
                null
            },
            title = { Text(text = title) },
            text = { Text(text = text) },
            confirmButton = {
                Button(onClick = onDismissRequest) {
                    Text(stringResource(id = R.string.ok))
                }
            },
            dismissButton = {
                if (isError) {
                    TextButton(
                        onClick = {
                            scope.launch {
                                val clip = ClipData.newPlainText("Error Text", text)
                                clipboardManager?.setPrimaryClip(clip)
                            }
                        }
                    ) {
                        Text(stringResource(id = R.string.copy))
                    }
                }
            }
        )
    }
}