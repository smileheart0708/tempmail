package com.temp.mail.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppDrawer() {
    ModalDrawerSheet {
        // TODO: Add dynamic email list here
        Text("Email list will be here", modifier = Modifier.padding(all = 16.dp))
    }
}