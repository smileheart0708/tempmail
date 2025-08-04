package com.temp.mail.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temp.mail.data.model.Email
import com.temp.mail.data.repository.EmailRepository
import com.temp.mail.data.repository.TokenRepository
import com.temp.mail.util.RefreshManager
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EmailListViewModel(
    private val emailRepository: EmailRepository,
    private val tokenRepository: TokenRepository,
    private val refreshManager: RefreshManager
) : ViewModel() {

    val emails: StateFlow<List<Email>> = emailRepository.emails
    val isLoading: StateFlow<Boolean> = emailRepository.isLoading
    val error: StateFlow<String?> = emailRepository.error

    init {
        viewModelScope.launch {
            refreshManager.refreshRequest.collectLatest { emailAddress ->
                refreshEmails(emailAddress)
            }
        }
    }
    
    fun loadEmails(emailAddress: String) {
        viewModelScope.launch {
            val authToken = tokenRepository.getCurrentToken()
            if (authToken != null) {
                val baseUrl = "https://api.mail.cx/api/v1" // 根据实际API调整
                emailRepository.loadEmails(baseUrl, emailAddress, authToken.token)
            } else {
                // 处理token为空的情况
                emailRepository.clearEmails()
            }
        }
    }
    
    fun refreshEmails(emailAddress: String) {
        loadEmails(emailAddress)
    }
    
    fun clearError() {
        emailRepository.clearError()
    }
    
    override fun onCleared() {
        super.onCleared()
        emailRepository.clearEmails()
    }
}