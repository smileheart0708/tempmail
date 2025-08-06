package com.temp.mail.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temp.mail.data.datastore.SettingsDataStore
import com.temp.mail.data.model.EmailDetails
import com.temp.mail.data.repository.EmailRepository
import com.temp.mail.data.repository.TokenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.File

class EmailDetailViewModel(
    private val emailRepository: EmailRepository,
    private val tokenRepository: TokenRepository,
   private val settingsDataStore: SettingsDataStore,
    private val context: Context
) : ViewModel() {

   val isJavaScriptEnabled: StateFlow<Boolean> = settingsDataStore.isJavaScriptEnabled
       .stateIn(
           scope = viewModelScope,
           started = SharingStarted.WhileSubscribed(5_000),
           initialValue = false
       )

    private val _emailDetails = MutableStateFlow<EmailDetails?>(null)
    val emailDetails: StateFlow<EmailDetails?> = _emailDetails.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val json = Json { ignoreUnknownKeys = true }

    fun loadEmailDetails(emailAddress: String?, emailId: String, isHistory: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = if (isHistory) {
                emailRepository.getHistoryEmailDetails(emailId)
            } else {
                if (emailAddress == null) {
                    _error.value = "Email address is required."
                    _isLoading.value = false
                    return@launch
                }
                val token = tokenRepository.getCurrentToken()?.token
                if (token == null) {
                    _error.value = "No valid token found."
                    _isLoading.value = false
                    return@launch
                }
                emailRepository.getEmailDetails(emailAddress, emailId, token)
            }

            result.onSuccess { details ->
                _emailDetails.value = details
            }.onFailure { exception ->
                _error.value = exception.message ?: "An unknown error occurred."
            }

            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}