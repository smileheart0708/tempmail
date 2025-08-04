package com.temp.mail.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthToken(
    val token: String,
    val expiresAt: Long = System.currentTimeMillis() + 300_000L // 5 minutes from now
) {
    fun isExpired(): Boolean {
        return System.currentTimeMillis() >= expiresAt
    }
    
    fun willExpireSoon(bufferSeconds: Long = 10): Boolean {
        return System.currentTimeMillis() >= (expiresAt - bufferSeconds * 1000)
    }
}