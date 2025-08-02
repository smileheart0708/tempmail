package com.temp.mail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.temp.mail.ui.screens.MainScreen
import com.temp.mail.ui.theme.TempMailTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            TempMailTheme {
                MainScreen()
            }
        }
    }
}
