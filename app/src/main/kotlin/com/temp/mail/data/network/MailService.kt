package com.temp.mail.data.network

import com.temp.mail.data.model.Email
import com.temp.mail.data.model.EmailDetails
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

interface MailService {
    suspend fun getEmailList(emailName: String, token: String): Result<List<Email>>
    suspend fun getEmailDetails(emailName: String, emailId: String, token: String): Result<EmailDetails>
}

class MailServiceImpl(private val baseUrl: String) : MailService {
    
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
    
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()
    
    override suspend fun getEmailList(
        emailName: String,
        token: String
    ): Result<List<Email>> = withContext(Dispatchers.IO) {
        try {
            val url = "$baseUrl/mailbox/$emailName"
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .build()
            
            val response = httpClient.newCall(request).execute()
            
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: "[]"
                val emails = json.decodeFromString<List<Email>>(responseBody)
                Result.success(emails)
            } else {
                Result.failure(IOException("HTTP ${response.code}: ${response.message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun getEmailDetails(
        emailName: String,
        emailId: String,
        token: String
    ): Result<EmailDetails> = withContext(Dispatchers.IO) {
        try {
            val url = "$baseUrl/mailbox/$emailName/$emailId"
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .build()

            val response = httpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    val emailDetails = json.decodeFromString<EmailDetails>(responseBody)
                    Result.success(emailDetails)
                } else {
                    Result.failure(IOException("Empty response body"))
                }
            } else {
                Result.failure(IOException("HTTP ${response.code}: ${response.message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}