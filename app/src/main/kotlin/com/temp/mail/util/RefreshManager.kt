package com.temp.mail.util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class RefreshManager {
    private val _refreshRequest = MutableSharedFlow<String>()
    val refreshRequest = _refreshRequest.asSharedFlow()

    suspend fun requestRefresh(emailAddress: String) {
        _refreshRequest.emit(emailAddress)
    }
}