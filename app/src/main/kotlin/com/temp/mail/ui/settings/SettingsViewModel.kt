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

    fun setTheme(theme: String) {
        viewModelScope.launch {
            settingsDataStore.setTheme(theme)
        }
    }
}