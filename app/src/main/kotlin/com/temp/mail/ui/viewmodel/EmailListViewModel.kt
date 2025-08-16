package com.temp.mail.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temp.mail.data.model.Email
import com.temp.mail.data.repository.EmailRepository
import com.temp.mail.data.repository.TokenRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class EmailListViewModel(
    private val emailRepository: EmailRepository,
    private val tokenRepository: TokenRepository
) : ViewModel() {

    val emails: StateFlow<List<Email>> = emailRepository.emails
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    val error: StateFlow<String?> = emailRepository.error

    private val _countdown = MutableStateFlow(10)
    val countdown: StateFlow<Int> = _countdown

    private var autoRefreshJob: Job? = null
    private var currentEmailAddress: String? = null

    companion object {
        private const val REFRESH_INTERVAL = 10_000L // 10 seconds
        private const val COUNTDOWN_SECONDS = 10
    }

    fun loadEmails(emailAddress: String) {
        currentEmailAddress = emailAddress
        refreshEmails(isManual = false)
    }

    fun refreshEmails(isManual: Boolean) {
        if (_isLoading.value) return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                currentEmailAddress?.let {
                    val authToken = tokenRepository.getCurrentToken()
                    if (authToken != null) {
                        emailRepository.loadEmails(it, authToken.token)
                    } else {
                        emailRepository.clearEmails()
                    }
                }
            } finally {
                _isLoading.value = false
                resetAutoRefreshTimer()
            }
        }
    }

    fun onActive() {
        resetAutoRefreshTimer()
    }

    fun onInactive() {
        autoRefreshJob?.cancel()
    }

    private fun resetAutoRefreshTimer() {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            _countdown.value = COUNTDOWN_SECONDS
            while (isActive && _countdown.value > 0) {
                delay(1000)
                _countdown.value--
            }
            if (isActive) {
                refreshEmails(isManual = false)
            }
        }
    }

    fun clearError() {
        emailRepository.clearError()
    }

    fun clearEmails(emailAddress: String) {
        viewModelScope.launch {
            emailRepository.clearEmails()
        }
    }

    override fun onCleared() {
        super.onCleared()
        autoRefreshJob?.cancel()
        emailRepository.clearEmails()
    }
}