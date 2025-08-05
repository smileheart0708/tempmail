package com.temp.mail.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temp.mail.data.datastore.SettingsDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val theme: StateFlow<String> = settingsDataStore.getTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = "System"
        )

    val isDynamicColor: StateFlow<Boolean> = settingsDataStore.isDynamicColor
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true
        )

   val isJavaScriptEnabled: StateFlow<Boolean> = settingsDataStore.isJavaScriptEnabled
       .stateIn(
           scope = viewModelScope,
           started = SharingStarted.WhileSubscribed(5_000),
           initialValue = false
       )

    fun setTheme(theme: String) {
        viewModelScope.launch {
            settingsDataStore.setTheme(theme)
        }
    }

    fun setDynamicColor(isDynamic: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setDynamicColor(isDynamic)
        }
    }

   fun setJavaScriptEnabled(isEnabled: Boolean) {
       viewModelScope.launch {
           settingsDataStore.setJavaScriptEnabled(isEnabled)
       }
   }
}