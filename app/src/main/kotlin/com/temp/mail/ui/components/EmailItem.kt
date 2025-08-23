package com.temp.mail.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.temp.mail.R
import com.temp.mail.data.model.Email
import com.temp.mail.ui.theme.ReadGreen
import com.temp.mail.ui.theme.UnreadRed
import com.temp.mail.util.FileLogger
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

@Composable
fun EmailItem(
    email: Email,
    onClick: () -> Unit
) {
    val fileLogger: FileLogger = koinInject()
    val coroutineScope = rememberCoroutineScope()
    val (formattedDate, formattedTime) = remember(email.date) {
        try {
            val instant = Instant.parse(email.date)
            val zonedDateTime = instant.atZone(ZoneId.systemDefault())
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.getDefault())
            Pair(zonedDateTime.format(dateFormatter), zonedDateTime.format(timeFormatter))
        } catch (e: DateTimeParseException) {
            // Asynchronously log the error to a file using the injected logger.
            coroutineScope.launch {
                fileLogger.logError(
                    tag = "EmailItem",
                    message = "Failed to parse date: ${email.date}",
                    throwable = e
                )
            }
            // Fallback for invalid date formats
            val parts = email.date.split("T")
            val datePart = parts.getOrNull(0) ?: email.date
            val timePart = parts.getOrNull(1)?.substringBefore(".") ?: ""
            Pair(datePart, timePart)
        }
    }

    val formattedFrom = remember(email.from) {
        val regex = Regex("<([^>]+)>")
        val matchResult = regex.find(email.from)
        matchResult?.groups?.get(1)?.value ?: email.from
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.from_sender_format, formattedFrom),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = email.subject,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomEnd
            ) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (email.seen) ReadGreen else UnreadRed
                ) {
                    Text(
                        text = if (email.seen) stringResource(R.string.status_read) else stringResource(R.string.status_unread),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}