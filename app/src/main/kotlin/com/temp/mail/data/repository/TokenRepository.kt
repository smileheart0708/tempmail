package com.temp.mail.data.repository

import android.content.Context
import android.util.Log
import com.temp.mail.data.model.AuthToken
import com.temp.mail.data.network.MailCxApiService
import com.temp.mail.util.NetworkUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicReference

class TokenRepository(
    private val apiService: MailCxApiService,
    private val context: Context
) {
    companion object {
        private const val TAG = "TokenRepository"
        private const val REFRESH_INTERVAL_MS = 290_000L // 290 seconds
    }

    private val _currentToken = MutableStateFlow<AuthToken?>(null)
    val currentToken: StateFlow<AuthToken?> = _currentToken.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val refreshJob = AtomicReference<Job?>(null)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun startTokenRefresh() {
        val job = scope.launch {
            while (isActive) {
                try {
                    refreshToken()
                    delay(REFRESH_INTERVAL_MS)
                } catch (e: Exception) {
                    Log.e(TAG, "Token refresh failed", e)
                    // Wait a shorter time before retrying on error
                    delay(30_000L) // 30 seconds
                }
            }
        }
        refreshJob.set(job)
    }

    fun stopTokenRefresh() {
        refreshJob.get()?.cancel()
        refreshJob.set(null)
    }

    suspend fun getCurrentToken(): AuthToken? {
        val token = _currentToken.value
        
        // If no token or expired, refresh immediately
        if (token == null || token.isExpired()) {
            refreshToken()
            return _currentToken.value
        }
        
        return token
    }

    private suspend fun refreshToken() {
        Log.d(TAG, "Refreshing token...")
        _error.value = null // Clear previous error

        if (!NetworkUtils.isNetworkAvailable(context)) {
            _error.value = "No internet connection"
            Log.w(TAG, "Token refresh skipped: No internet connection.")
            return
        }

        apiService.fetchRawToken()
            .onSuccess { rawToken ->
                // The raw token is a string with quotes and potentially leading/trailing whitespace (like newlines).
                val cleanedToken = rawToken.trim().trim('"')
                if (cleanedToken.isNotBlank()) {
                    val authToken = AuthToken(token = cleanedToken)
                    _currentToken.value = authToken
                    Log.d(TAG, "Token refreshed successfully.")
                } else {
                    val errorMessage = "Received empty token"
                    Log.e(TAG, errorMessage)
                    _error.value = errorMessage
                }
            }
            .onFailure { error ->
                val errorMessage = error.message ?: "Unknown error while refreshing token"
                Log.e(TAG, "Failed to refresh token: $errorMessage", error)
                _error.value = errorMessage
                // We don't rethrow the error here to prevent crashing the refresh loop.
                // The error is exposed via the `error` StateFlow.
            }
    }

    fun cleanup() {
        stopTokenRefresh()
        scope.cancel()
    }
}