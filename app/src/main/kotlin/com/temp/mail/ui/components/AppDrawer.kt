package com.temp.mail.ui.components

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.temp.mail.R
import com.temp.mail.ui.settings.SettingsActivity

@Composable
fun AppDrawer() {
    ModalDrawerSheet {
        Column(modifier = Modifier.fillMaxHeight()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Home item
            NavigationDrawerItem(
                icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                label = { Text(stringResource(id = R.string.menu_home)) },
                selected = true,
                onClick = { /* TODO: Navigate to home */ },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            // Gallery item
            NavigationDrawerItem(
                icon = { Icon(Icons.Filled.Info, contentDescription = null) },
                label = { Text(stringResource(id = R.string.menu_gallery)) },
                selected = false,
                onClick = { /* TODO: Navigate to gallery */ },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            // Slideshow item
            NavigationDrawerItem(
                icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                label = { Text(stringResource(id = R.string.menu_slideshow)) },
                selected = false,
                onClick = { /* TODO: Navigate to slideshow */ },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Settings item
            val context = LocalContext.current
            NavigationDrawerItem(
                icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                label = { Text(stringResource(id = R.string.action_settings)) },
                selected = false,
                onClick = {
                    context.startActivity(Intent(context, SettingsActivity::class.java))
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}