package com.temp.mail.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import android.webkit.WebView
import androidx.compose.ui.viewinterop.AndroidView
import com.temp.mail.R
import com.temp.mail.ui.theme.TempMailTheme
import com.temp.mail.ui.viewmodel.EmailDetailViewModel
import org.koin.androidx.compose.koinViewModel

class EmailDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val emailId = intent.getStringExtra("EMAIL_ID")
        val emailAddress = intent.getStringExtra("EMAIL_ADDRESS")

        setContent {
            TempMailTheme {
                EmailDetailScreen(
                    emailId = emailId,
                    emailAddress = emailAddress,
                    onNavigateUp = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailDetailScreen(
    emailId: String?,
    emailAddress: String?,
    viewModel: EmailDetailViewModel = koinViewModel(),
    onNavigateUp: () -> Unit
) {
    val emailDetails by viewModel.emailDetails.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    androidx.compose.runtime.LaunchedEffect(emailId, emailAddress) {
        if (emailId != null && emailAddress != null) {
            val emailName = emailAddress.split("@").firstOrNull()
            if (emailName != null) {
                viewModel.loadEmailDetails(emailName, emailId)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = emailDetails?.subject ?: stringResource(id = R.string.email_details)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_up)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }
                error != null -> {
                    Text(
                        text = error ?: stringResource(id = R.string.unknown_error),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                emailDetails != null -> {
                    AndroidView(
                        factory = { context ->
                            WebView(context).apply {
                                settings.javaScriptEnabled = true
                            }
                        },
                        update = { webView ->
                            webView.loadDataWithBaseURL(
                                null,
                                emailDetails!!.body,
                                "text/html",
                                "UTF-8",
                                null
                            )
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    Text(
                        text = stringResource(id = R.string.no_email_details),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}