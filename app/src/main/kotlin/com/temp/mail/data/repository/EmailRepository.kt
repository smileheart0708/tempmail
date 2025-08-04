package com.temp.mail.data.repository

import com.temp.mail.data.model.Email
import com.temp.mail.data.network.MailService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface EmailRepository {
    val emails: StateFlow<List<Email>>
    val isLoading: StateFlow<Boolean>
    val error: StateFlow<String?>
    
    suspend fun loadEmails(baseUrl: String, emailName: String, token: String)
    fun clearEmails()
    fun clearError()
}

class EmailRepositoryImpl(
    private val mailService: MailService,
    private val tokenRepository: TokenRepository
) : EmailRepository {

    private val _emails = MutableStateFlow<List<Email>>(emptyList())
    override val emails: StateFlow<List<Email>> = _emails.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    override val error: StateFlow<String?> = _error.asStateFlow()

    override suspend fun loadEmails(baseUrl: String, emailName: String, token: String) {
        _isLoading.value = true
        _error.value = null

        try {
            val result = mailService.getEmailList(baseUrl, emailName, token)
            result.onSuccess { emailList ->
                _emails.value = emailList
            }.onFailure { exception ->
                if (exception.message?.contains("401") == true) {
                    if (tokenRepository.refreshToken()) {
                        val newToken = tokenRepository.getCurrentToken()?.token
                        if (newToken != null) {
                            loadEmails(baseUrl, emailName, newToken)
                        } else {
                            _error.value = "Failed to refresh token"
                        }
                    } else {
                        _error.value = "Failed to refresh token"
                    }
                } else {
                    _error.value = exception.message ?: "Unknown error occurred"
                }
            }
        } finally {
            _isLoading.value = false
        }
    }

    override fun clearEmails() {
        _emails.value = emptyList()
    }
    
    override fun clearError() {
        _error.value = null
    }
}