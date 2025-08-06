package com.temp.mail.ui.history

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.temp.mail.R
import com.temp.mail.ui.EmailDetailActivity
import com.temp.mail.ui.screens.EmailItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun HistoryScreen(modifier: Modifier = Modifier, viewModel: HistoryViewModel = koinViewModel()) {
    val historyMails by viewModel.historyMails.collectAsState()
    val context = LocalContext.current

    if (historyMails.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(id = R.string.no_history_mails))
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(historyMails) { email ->
                EmailItem(
                    email = email,
                    onClick = {
                        val intent = Intent(context, EmailDetailActivity::class.java).apply {
                            putExtra("EMAIL_ID", email.id)
                            putExtra("IS_HISTORY", true)
                        }
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}