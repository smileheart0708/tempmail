package com.temp.mail.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temp.mail.data.model.Email
import com.temp.mail.data.repository.EmailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(private val emailRepository: EmailRepository) : ViewModel() {

    private val _historyMails = MutableStateFlow<List<Email>>(emptyList())
    val historyMails: StateFlow<List<Email>> = _historyMails.asStateFlow()

    init {
        loadHistoryMails()
    }

    private fun loadHistoryMails() {
        viewModelScope.launch {
            emailRepository.loadHistoryEmails().onSuccess {
                _historyMails.value = it
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            emailRepository.clearHistory()
            loadHistoryMails()
        }
    }
}