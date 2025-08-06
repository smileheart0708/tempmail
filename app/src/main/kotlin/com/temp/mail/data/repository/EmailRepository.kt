package com.temp.mail.data.repository

import android.content.Context
import com.temp.mail.data.model.Email
import com.temp.mail.data.model.EmailBody
import com.temp.mail.data.model.EmailDetails
import com.temp.mail.data.network.MailService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

interface EmailRepository {
    val emails: StateFlow<List<Email>>
    val isLoading: StateFlow<Boolean>
    val error: StateFlow<String?>

    suspend fun loadEmails(emailAddress: String, token: String)
    suspend fun getEmailDetails(emailAddress: String, emailId: String, token: String): Result<EmailDetails>
    suspend fun loadHistoryEmails(): Result<List<Email>>
    suspend fun getHistoryEmailDetails(emailId: String): Result<EmailDetails>
    fun clearEmails()
    fun clearError()
}

class EmailRepositoryImpl(
    private val mailService: MailService,
    private val tokenRepository: TokenRepository,
    private val context: Context
) : EmailRepository {

    private val _emails = MutableStateFlow<List<Email>>(emptyList())
    override val emails: StateFlow<List<Email>> = _emails.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    override val error: StateFlow<String?> = _error.asStateFlow()

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun loadEmails(emailAddress: String, token: String) {
        _isLoading.value = true
        _error.value = null

        try {
            val result = mailService.getEmailList(emailAddress, token)
            result.onSuccess { emailList ->
                _emails.value = emailList
            }.onFailure { exception ->
                if (exception.message?.contains("401") == true) {
                    if (tokenRepository.refreshToken()) {
                        val newToken = tokenRepository.getCurrentToken()?.token
                        if (newToken != null) {
                            loadEmails(emailAddress, newToken)
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

    override suspend fun getEmailDetails(emailAddress: String, emailId: String, token: String): Result<EmailDetails> {
        val result = mailService.getEmailDetails(emailAddress, emailId, token)
        result.onSuccess { details ->
            withContext(Dispatchers.IO) {
                try {
                    val file = File(context.cacheDir, "$emailId.json")
                    val jsonString = json.encodeToString(details)
                    file.writeText(jsonString)
                } catch (e: Exception) {
                    // Log or handle exception
                }
            }
        }
        return result
    }

    override suspend fun loadHistoryEmails(): Result<List<Email>> = withContext(Dispatchers.IO) {
        try {
            val cacheDir = context.cacheDir
            val jsonFiles = cacheDir.listFiles { _, name -> name.endsWith(".json") }
            val emails = jsonFiles?.mapNotNull { file ->
                try {
                    val jsonString = file.readText()
                    val details = json.decodeFromString<EmailDetails>(jsonString)
                    Email(
                        mailbox = "", // Not available in cached details
                        id = details.id,
                        from = details.from,
                        to = details.to,
                        subject = details.subject,
                        date = details.date,
                        posixMillis = 0, // Not available
                        size = 0, // Not available
                        seen = true // Assume seen if cached
                    )
                } catch (e: Exception) {
                    null
                }
            } ?: emptyList()
            Result.success(emails)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getHistoryEmailDetails(emailId: String): Result<EmailDetails> = withContext(Dispatchers.IO) {
        try {
            val file = File(context.cacheDir, "$emailId.json")
            if (file.exists()) {
                val jsonString = file.readText()
                val emailDetails = json.decodeFromString<EmailDetails>(jsonString)
                Result.success(emailDetails)
            } else {
                Result.failure(Exception("Email not found in cache"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun clearEmails() {
        _emails.value = emptyList()
    }

    override fun clearError() {
        _error.value = null
    }
}