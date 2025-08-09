package com.temp.mail.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temp.mail.data.model.EmailAddress
import com.temp.mail.data.repository.EmailRepository
import com.temp.mail.data.repository.TokenRepository
import com.temp.mail.util.EmailGenerator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel(
    private val context: Context,
    private val tokenRepository: TokenRepository,
    private val emailRepository: EmailRepository
) : ViewModel() {

    private val _emailAddresses = MutableStateFlow<List<EmailAddress>>(emptyList())
    val emailAddresses: StateFlow<List<EmailAddress>> = _emailAddresses.asStateFlow()

    private val _selectedEmailAddress = MutableStateFlow<EmailAddress?>(null)
    val selectedEmailAddress: StateFlow<EmailAddress?> = _selectedEmailAddress.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        // Start refreshing the token as soon as the ViewModel is created.
        tokenRepository.startTokenRefresh()
        
        // Observe errors from the TokenRepository.
        viewModelScope.launch {
            tokenRepository.error.collect { errorMessage ->
                _error.value = errorMessage
            }
        }
        
        // TODO: Replace with actual logic to load email addresses from a repository.
        // For now, we'll keep the sample data logic but trigger it from here.
        loadInitialEmail()
    }
    
    fun selectEmailAddress(emailAddress: EmailAddress) {
        viewModelScope.launch {
            // 更新选中状态
            val updatedList = _emailAddresses.value.map { email ->
                email.copy(isActive = email.id == emailAddress.id)
            }
            _emailAddresses.value = updatedList
            _selectedEmailAddress.value = emailAddress
        }
    }
    
    fun addEmailAddress() {
        viewModelScope.launch {
            val newEmail = EmailAddress(
                id = UUID.randomUUID().toString(),
                address = EmailGenerator.generateRandomEmail(context),
                isActive = false
            )
            val updatedList = _emailAddresses.value + newEmail
            _emailAddresses.value = updatedList
            // 自动选择新添加的邮箱
            selectEmailAddress(newEmail)
        }
    }
    
    fun removeEmailAddress(emailAddress: EmailAddress) {
        viewModelScope.launch {
            _emailAddresses.value = _emailAddresses.value.filter { it.id != emailAddress.id }
            // 如果删除的是当前选中的邮箱，选择第一个邮箱
            if (_selectedEmailAddress.value?.id == emailAddress.id) {
                _selectedEmailAddress.value = _emailAddresses.value.firstOrNull()
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    override fun onCleared() {
        super.onCleared()
        // Stop the token refresh when the ViewModel is destroyed to prevent leaks.
        tokenRepository.stopTokenRefresh()
    }
    
    private fun loadInitialEmail() {
        // This is a placeholder. In a real app, you would load saved emails.
        // If no emails exist, you might create one.
        if (_emailAddresses.value.isEmpty()) {
            addEmailAddress()
        } else {
            _selectedEmailAddress.value = _emailAddresses.value.first()
        }
    }
}