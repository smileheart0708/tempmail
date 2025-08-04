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
    val isLoading: StateFlow<Boolean> = emailRepository.isLoading
    val error: StateFlow<String?> = emailRepository.error

    private var autoRefreshJob: Job? = null
    private var currentEmailAddress: String? = null

    companion object {
        private const val REFRESH_INTERVAL = 10_000L // 10 seconds
    }


    fun loadEmails(emailAddress: String) {
        currentEmailAddress = emailAddress
        viewModelScope.launch {
            val authToken = tokenRepository.getCurrentToken()
            if (authToken != null) {
                emailRepository.loadEmails(emailAddress, authToken.token)
                onActive()
            } else {
                // 处理token为空的情况
                emailRepository.clearEmails()
            }
        }
    }

    fun refreshEmails(emailAddress: String) {
        loadEmails(emailAddress)
    }

    fun onActive() {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (isActive) {
                delay(REFRESH_INTERVAL)
                currentEmailAddress?.let {
                    val authToken = tokenRepository.getCurrentToken()
                    if (authToken != null) {
                        emailRepository.loadEmails(it, authToken.token)
                    }
                }
            }
        }
    }

    fun onInactive() {
        autoRefreshJob?.cancel()
    }

    fun clearError() {
        emailRepository.clearError()
    }

    override fun onCleared() {
        super.onCleared()
        autoRefreshJob?.cancel()
        emailRepository.clearEmails()
    }
}