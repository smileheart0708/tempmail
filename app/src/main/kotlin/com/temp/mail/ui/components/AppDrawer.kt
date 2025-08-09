package com.temp.mail.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.temp.mail.R
import com.temp.mail.data.model.EmailAddress
import com.temp.mail.ui.settings.SettingsActivity
import com.temp.mail.ui.history.HistoryActivity

@Composable
fun AppDrawer(
    emailAddresses: List<EmailAddress> = emptyList(),
    selectedEmailAddress: EmailAddress? = null,
    onEmailAddressSelected: (EmailAddress) -> Unit = {},
    onAddEmailClick: () -> Unit = {}
) {
    ModalDrawerSheet {
        Column(modifier = Modifier.fillMaxHeight()) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // 标题栏
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.email_addresses),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = stringResource(id = R.string.create_new_email),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(end = 8.dp)
                )
                SmallFloatingActionButton(
                    onClick = onAddEmailClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = stringResource(id = R.string.add_email),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // 邮箱地址列表
            if (emailAddresses.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    items(emailAddresses, key = { it.address }) { emailAddress ->
                        NavigationDrawerItem(
                            icon = { 
                                Icon(
                                    Icons.Filled.Email, 
                                    contentDescription = null,
                                    tint = if (emailAddress.isActive) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                ) 
                            },
                            label = { 
                                Text(
                                    text = emailAddress.address,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                ) 
                            },
                            selected = emailAddress.isActive,
                            onClick = { onEmailAddressSelected(emailAddress) },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            } else {
                // 空状态显示
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Icon(
                        Icons.Filled.Email,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(id = R.string.no_email_addresses),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(id = R.string.add_email_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 历史邮件和设置项
            val context = LocalContext.current
            Row(modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)) {
                NavigationDrawerItem(
                    icon = { Icon(Icons.Filled.History, contentDescription = null) },
                    label = { Text(stringResource(id = R.string.history_mails)) },
                    selected = false,
                    onClick = {
                        context.startActivity(Intent(context, HistoryActivity::class.java))
                    },
                    modifier = Modifier.weight(1f)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                    label = { Text(stringResource(id = R.string.action_settings)) },
                    selected = false,
                    onClick = {
                        context.startActivity(Intent(context, SettingsActivity::class.java))
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}