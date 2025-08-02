package com.temp.mail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.temp.mail.ui.screens.MainScreen
import com.temp.mail.ui.theme.TempMailTheme
import androidx.core.view.WindowCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            TempMailTheme {
                MainScreen()
            }
        }
    }
}
