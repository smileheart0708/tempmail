package com.temp.mail.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temp.mail.data.model.Email
import com.temp.mail.data.repository.EmailRepository
import com.temp.mail.data.repository.TokenRepository
import com.temp.mail.util.RefreshManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class EmailListViewModel(
    private val emailRepository: EmailRepository,
    private val tokenRepository: TokenRepository,
    private val refreshManager: RefreshManager
) : ViewModel() {

    val emails: StateFlow<List<Email>> = emailRepository.emails
    val isLoading: StateFlow<Boolean> = emailRepository.isLoading
    val error: StateFlow<String?> = emailRepository.error

    private var autoRefreshJob: Job? = null
    private var currentEmailAddress: String? = null

    companion object {
        private const val REFRESH_INTERVAL = 10_000L // 10 seconds
    }

    init {
        viewModelScope.launch {
            refreshManager.refreshRequest.collectLatest { emailAddress ->
                refreshEmails(emailAddress)
            }
        }
    }

    fun loadEmails(emailAddress: String) {
        currentEmailAddress = emailAddress
        viewModelScope.launch {
            val authToken = tokenRepository.getCurrentToken()
            if (authToken != null) {
                val baseUrl = "https://api.mail.cx/api/v1" // 请求API地址
                emailRepository.loadEmails(baseUrl, emailAddress, authToken.token)
                startAutoRefresh()
            } else {
                // 处理token为空的情况
                emailRepository.clearEmails()
            }
        }
    }

    fun refreshEmails(emailAddress: String) {
        loadEmails(emailAddress)
        resetAutoRefresh()
    }

    private fun startAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (isActive) {
                delay(REFRESH_INTERVAL)
                currentEmailAddress?.let {
                    loadEmails(it)
                }
            }
        }
    }

    private fun resetAutoRefresh() {
        startAutoRefresh()
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