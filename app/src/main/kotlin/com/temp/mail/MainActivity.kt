package com.temp.mail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.temp.mail.ui.screens.MainScreen
import com.temp.mail.ui.settings.SettingsViewModel
import com.temp.mail.ui.theme.TempMailTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: SettingsViewModel = koinViewModel()
            val theme by viewModel.theme.collectAsState()
            TempMailTheme(
                darkTheme = when (theme) {
                    "Light" -> false
                    "Dark" -> true
                    else -> resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_YES
                }
           ) {
               MainScreen()
           }
       }
   }
}
