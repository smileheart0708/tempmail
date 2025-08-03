package com.temp.mail.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temp.mail.data.model.EmailAddress
import com.temp.mail.util.EmailGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel(
    private val context: Context
) : ViewModel() {

    private val _emailAddresses = MutableStateFlow<List<EmailAddress>>(emptyList())
    val emailAddresses: StateFlow<List<EmailAddress>> = _emailAddresses.asStateFlow()
    
    private val _selectedEmailAddress = MutableStateFlow<EmailAddress?>(null)
    val selectedEmailAddress: StateFlow<EmailAddress?> = _selectedEmailAddress.asStateFlow()
    
    init {
        // 示例数据，后续可以从网络或本地存储加载
        loadSampleData()
    }
    
    private fun loadSampleData() {
        viewModelScope.launch {
            val sampleEmails = List(2) {
                EmailAddress(
                    id = UUID.randomUUID().toString(),
                    address = EmailGenerator.generateRandomEmail(context),
                    isActive = it == 0
                )
            }
            _emailAddresses.value = sampleEmails
            _selectedEmailAddress.value = sampleEmails.firstOrNull { it.isActive }
        }
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
}