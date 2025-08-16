@file:OptIn(ExperimentalMaterial3Api::class)

package com.temp.mail.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
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


    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // 在列表滚动前不消耗任何滚动
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // 在列表滚动后，如果还有可用的向下滚动，则触发刷新
                if (available.y > 0) {
                    viewModel.refreshEmails(isManual = true)
                }
                return Offset.Zero
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().nestedScroll(nestedScrollConnection)) {
        // 统一的加载指示器，在首次加载和手动刷新时都显示
        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        when {
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
                            viewModel.refreshEmails(isManual = true)
                        }
                    ) {
                        Text(stringResource(R.string.retry))
                    }
                }
            }
            // 默认显示邮件列表或空状态，加载指示器在外部处理
            else -> {
                if (emails.isEmpty()) {
                    EmptyState()
                } else {
                    EmailListContent(
                        emails = emails,
                        emailAddress = emailAddress
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState() {
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

@Composable
private fun EmailListContent(
    emails: List<Email>,
    emailAddress: String
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(emails) { email ->
            val context = LocalContext.current
            EmailItem(
                email = email,
                onClick = {
                    val intent =
                        Intent(
                            context,
                            EmailDetailActivity::class.java
                        ).apply {
                            putExtra("EMAIL_ID", email.id)
                            putExtra("EMAIL_ADDRESS", emailAddress)
                        }
                    context.startActivity(intent)
                }
            )
        }
    }
}