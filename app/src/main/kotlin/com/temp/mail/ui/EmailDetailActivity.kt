package com.temp.mail.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import android.annotation.SuppressLint
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.ui.viewinterop.AndroidView
import com.temp.mail.R
import com.temp.mail.ui.theme.TempMailTheme
import com.temp.mail.ui.viewmodel.EmailDetailViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import android.content.ClipData
import android.content.ClipboardManager
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

class EmailDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val emailId = intent.getStringExtra("EMAIL_ID")
        val emailAddress = intent.getStringExtra("EMAIL_ADDRESS")
        val isHistory = intent.getBooleanExtra("IS_HISTORY", false)

        setContent {
            TempMailTheme {
                EmailDetailScreen(
                    emailId = emailId,
                    emailAddress = emailAddress,
                    isHistory = isHistory,
                    onNavigateUp = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun EmailDetailScreen(
    emailId: String?,
    emailAddress: String?,
    isHistory: Boolean,
    viewModel: EmailDetailViewModel = koinViewModel(),
    onNavigateUp: () -> Unit
) {
    val emailDetails by viewModel.emailDetails.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isJavaScriptEnabled by viewModel.isJavaScriptEnabled.collectAsState()
    val verificationCode by viewModel.verificationCode.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(ClipboardManager::class.java)

    androidx.compose.runtime.LaunchedEffect(emailId, emailAddress, isHistory) {
        if (emailId != null) {
            viewModel.loadEmailDetails(emailAddress, emailId, isHistory)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                },
                actions = {
                    if (verificationCode != null) {
                        val message = stringResource(id = R.string.verification_code_copied, verificationCode!!)
                        IconButton(onClick = {
                            scope.launch {
                                val clip = ClipData.newPlainText("Verification Code", verificationCode!!)
                                clipboardManager?.setPrimaryClip(clip)
                                snackbarHostState.showSnackbar(
                                    message = message,
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = stringResource(id = R.string.copy_verification_code)
                            )
                        }
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
                               settings.apply {
                                   javaScriptEnabled = isJavaScriptEnabled
                                   domStorageEnabled = false
                                   allowFileAccess = false
                                   allowContentAccess = false
                                   cacheMode = WebSettings.LOAD_NO_CACHE
                                   javaScriptCanOpenWindowsAutomatically = false
                                   blockNetworkImage = false
                                   loadsImagesAutomatically = true
                                   mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                               }
                           }
                       },
                       update = { webView ->
                           emailDetails?.body?.html?.let {
                               webView.loadDataWithBaseURL(
                                   null,
                                   it,
                                   "text/html",
                                   "UTF-8",
                                   null
                               )
                           }
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