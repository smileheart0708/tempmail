@file:OptIn(ExperimentalMaterial3Api::class)

package com.temp.mail.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.temp.mail.R
import com.temp.mail.data.model.Email
import com.temp.mail.ui.EmailDetailActivity
import com.temp.mail.ui.components.EmailItem
import com.temp.mail.ui.viewmodel.EmailListViewModel
import com.temp.mail.util.FileLogger
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

@Composable
fun EmailListScreen(
    emailAddress: String,
    viewModel: EmailListViewModel = koinViewModel()
) {
    val emails by viewModel.emails.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(emailAddress) {
        viewModel.loadEmails(emailAddress)
    }

    DisposableEffect(viewModel) {
        viewModel.onActive()
        onDispose {
            viewModel.onInactive()
        }
    }

    var showClearDialog by remember { mutableStateOf(false) }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(stringResource(id = R.string.clear_all_emails)) },
            text = { Text(stringResource(id = R.string.clear_all_emails_confirmation)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearEmails(emailAddress)
                        showClearDialog = false
                    }
                ) {
                    Text(stringResource(id = R.string.clear))
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }

    PullToRefreshBox(
        isRefreshing = isLoading,
        onRefresh = { viewModel.refreshEmails(emailAddress) }
    ) {
            when {
                isLoading && emails.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
                error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.load_failed),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                viewModel.clearError()
                                viewModel.refreshEmails(emailAddress)
                            }
                        ) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
                emails.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Email,
                            contentDescription = stringResource(R.string.no_emails_received),
                            modifier = Modifier.size(128.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.no_emails_received),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(R.string.auto_refresh_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(emails) { email ->
                            val context = LocalContext.current
                            EmailItem(
                                email = email,
                                onClick = {
                                    val intent = Intent(context, EmailDetailActivity::class.java).apply {
                                        putExtra("EMAIL_ID", email.id)
                                        putExtra("EMAIL_ADDRESS", emailAddress)
                                    }
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }
            }
    }
}