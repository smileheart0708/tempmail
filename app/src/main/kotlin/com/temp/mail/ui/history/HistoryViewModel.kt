package com.temp.mail.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temp.mail.data.model.Email
import com.temp.mail.data.repository.EmailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(private val emailRepository: EmailRepository) : ViewModel() {
    private val _historyMails = MutableStateFlow<List<Email>>(emptyList())
    val historyMails = _historyMails.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            val result = emailRepository.loadHistoryEmails()
            result.onSuccess {
                _historyMails.value = it
            }
        }
    }
}